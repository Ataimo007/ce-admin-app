package com.global.tech.space.cechurchadmin.attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.dialogs.CellBottomSheets;
import com.global.tech.space.cechurchadmin.dialogs.RankBottomSheets;
import com.global.tech.space.cechurchadmin.helpers.Helper;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.Member;
import com.global.tech.space.cechurchadmin.models.ExistanceInfo;
import com.global.tech.space.cechurchadmin.models.ModelResponse;
import com.global.tech.space.cechurchadmin.models.RegisteredMember;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterMemberActivity extends AppCompatActivity implements CellBottomSheets.CellSellectionHandler, RankBottomSheets.RankSellectionHandler
{

//    public static final int ADD_MEMBER = 2001;
    public static final String REG_FAIL = "reg_fail";
    public static final String EDIT_MEMBER = "edit_member";

//    private final NameValidator validator = new NameValidator();

    private CEService.CEApi ceApi;
    private LocalDate cdob;
    private CellBottomSheets cells;
    private RankBottomSheets ranks;
    private Member member;

    private boolean exist = false;
    private Cell selectedCell;
    private Member.Rank selectedRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initViews();
        processIntent();
    }

    private void processIntent() {
        if ( getIntent().getAction() != null && getIntent().getAction().equals(EDIT_MEMBER) )
        {
            initView();
            initValues();
        }
    }

    private void initRank()
    {
        TextInputLayout rank = findViewById(R.id.reg_rank);
        TextInputEditText rankField = findViewById(R.id.regs_rank);
        rank.setVisibility( View.VISIBLE );
        rankField.setCursorVisible( false );
        rankField.setOnTouchListener( (v, event) -> {
            if  ( event.getAction() == MotionEvent.ACTION_UP )
            {
                ranks = new RankBottomSheets();
                ranks.show( getSupportFragmentManager() );
            }

            return rankField.performClick();

        } );
    }

    private void initView() {
        FloatingActionButton edit = findViewById(R.id.reg_action);
        edit.setImageResource( R.drawable.ic_edit_black_24dp );
    }

    private void initValues() {
        member = Member.creator.fromGson(getIntent().getStringExtra( "member" ));
        ( (TextInputEditText) findViewById(R.id.regs_first_name) ).setText( member.firstName );
        ( (TextInputEditText) findViewById(R.id.regs_last_name) ).setText( member.surname );
        ( (TextInputEditText) findViewById(R.id.regs_other_names) ).setText( member.otherNames );
        ( (TextInputEditText) findViewById(R.id.regs_phone) ).setText( member.phoneNumber );
        ( (TextInputEditText) findViewById(R.id.regs_email) ).setText( member.emailAddress );
        cdob = member.dateOfBirth.getDate();
        ( (TextInputEditText) findViewById(R.id.regs_dob) ).setText( member.dateOfBirth.toString() );
        ( (TextInputEditText) findViewById(R.id.regs_address) ).setText( member.homeAddress );
        if ( member.gender.equals(Member.Gender.MALE) )
            ((RadioButton) findViewById(R.id.gender_male)).setChecked( true );
        else
            ((RadioButton) findViewById(R.id.gender_female)).setChecked( true );
        if (member.kingsChatNo != null && !member.kingsChatNo.isEmpty())
        {
            if ( member.phoneNumber.equals( member.kingsChatNo ) )
                ( ( CheckBox ) findViewById(R.id.reg_is_king) ).setChecked( true );
            else
            {
                ( (TextInputEditText) findViewById(R.id.regs_kings_chat) ).setText( member.kingsChatNo );
                ( ( CheckBox ) findViewById(R.id.reg_is_king) ).setChecked( false );
            }
        }
        selectedCell = member.cell;
        selectedRank = member.rank;
        if ( selectedCell != null )
            ( (TextInputEditText) findViewById(R.id.regs_cell) ).setText( selectedCell.name );
        if ( selectedRank != null )
            ( (TextInputEditText) findViewById(R.id.regs_cell) ).setText( selectedRank.name );

//        member.cell = null;
    }

    private void initViews() {
        initDob();
        initYear();
        initCell();
        initRank();
        initKingsCheck();
        initToolbar();
        initValidation();
    }

    private void initValidation() {
        if ( getIntent().getAction() != null && getIntent().getAction().equals(EDIT_MEMBER) )
            return;

        View.OnFocusChangeListener validation = (v, hasFocus) -> {
            Log.d( "Register_Member", "Validating members existence" );
            if (!hasFocus)
            {
                String first = ( (TextInputEditText) findViewById(R.id.regs_first_name) ).getText().toString();
                String last = ( (TextInputEditText) findViewById(R.id.regs_last_name) ).getText().toString();
                if ( !first.isEmpty() && !last.isEmpty())
                    ifMemberExist();
            }
        };

        findViewById(R.id.regs_first_name).setOnFocusChangeListener(validation);
        findViewById(R.id.regs_last_name).setOnFocusChangeListener(validation);
        findViewById(R.id.regs_other_names).setOnFocusChangeListener(validation);
        findViewById(R.id.regs_phone).setOnFocusChangeListener(validation);
    }

    private void initToolbar() {
        CollapsingToolbarLayout toolbar = findViewById(R.id.user_reg_collapse);
        toolbar.setCollapsedTitleTextColor(ResourcesCompat.getColor( getResources(), R.color.white, null ));
    }

    private void initKingsCheck() {
        CheckBox isKing = findViewById(R.id.reg_is_king);
        isKing.setOnCheckedChangeListener( (buttonView, isChecked) -> {
            if ( isChecked )
                findViewById( R.id.reg_kings_chat ).setVisibility( View.GONE );
            else
                findViewById( R.id.reg_kings_chat ).setVisibility( View.VISIBLE );
        });
    }

    @Override
    public void onSelectCell(Cell info) {
        TextView cell = findViewById(R.id.regs_cell);
        cell.setText( info.name );
        selectedCell = info;
    }

    @Override
    public void onSelectRank(Member.Rank info) {
        TextView cell = findViewById(R.id.regs_rank);
        cell.setText( info.name );
        selectedRank = info;
    }

    private void initCell() {
        TextView cell = findViewById(R.id.regs_cell);
        cell.setCursorVisible( false );
        cell.setOnTouchListener( (v, event) -> {
            if  ( event.getAction() == MotionEvent.ACTION_UP )
            {
                cells = new CellBottomSheets();
                cells.show( getSupportFragmentManager() );
            }

            return cell.performClick();

        } );
    }

    private void initYear() {
        TextInputEditText dob = findViewById( R.id.regs_dob );
        CheckBox ignore = findViewById(R.id.reg_ignore_year);
        ignore.setOnCheckedChangeListener( (buttonView, isChecked) -> {
            if ( cdob != null )
            {
                if ( isChecked )
                    dob.setText( cdob.toString( DateTimeFormat.forPattern("EEEE dd, MMM.") ) );
                else
                    dob.setText( cdob.toString( DateTimeFormat.forPattern("EEEE dd, MMM yyyy.") ) );
            }
        });
    }

    private void initDob() {
        TextInputEditText dob = findViewById( R.id.regs_dob );
        dob.setCursorVisible( false );
        dob.setOnTouchListener( (v, event) -> {
            if ( event.getAction() == MotionEvent.ACTION_UP )
            {
                CheckBox ignore = findViewById(R.id.reg_ignore_year);
                LocalDate date = cdob == null ? LocalDate.now() : cdob;
                DatePickerDialog picker = new DatePickerDialog(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert,
                        (view, year, month, dayOfMonth) ->
                        {
                            cdob = LocalDate.parse(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth),
                                    DateTimeFormat.forPattern("yyyy-MM-dd"));
                            dob.setText( ignore.isChecked() ? cdob.toString( DateTimeFormat.forPattern("EEEE dd, MMMM.") ) :
                                    cdob.toString( DateTimeFormat.forPattern("EEEE dd, MMMM yyyy.") ));
                        },
                        date.getYear(), date.getMonthOfYear(), date.getDayOfMonth() );
                picker.setTitle( "Select Date Of Birth" );
                picker.show();
            }
            return dob.performClick();
        });
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
    }

    public void register(View view) {
        String first = ( (TextInputEditText) findViewById(R.id.regs_first_name) ).getText().toString();
        first = first.isEmpty() ? null : first;
        String last = ( (TextInputEditText) findViewById(R.id.regs_last_name) ).getText().toString();
        last = last.isEmpty() ? null : last;
        String other = ( (TextInputEditText) findViewById(R.id.regs_other_names) ).getText().toString();
        other = other.isEmpty() ? null : other;
        String phone = ( (TextInputEditText) findViewById(R.id.regs_phone) ).getText().toString();
        phone = phone.isEmpty() ? null : phone;
        String email = ( (TextInputEditText) findViewById(R.id.regs_email) ).getText().toString();
        email = email.isEmpty() ? null : email;
        Member.DateOfBirth dob = cdob == null ? null : new Member.DateOfBirth( cdob, ((CheckBox) findViewById(R.id.reg_ignore_year)).isChecked() );
        String address = ( (TextInputEditText) findViewById(R.id.regs_address) ).getText().toString();
        address = address.isEmpty() ? null : address;

        Member.Gender gender = null;
        RadioGroup genders = findViewById(R.id.reg_gender);
        int genderCheck = genders.getCheckedRadioButtonId();
        if ( genderCheck > -1 )
        {
            String genderName = ((RadioButton) genders.findViewById(genderCheck)).getText().toString().toLowerCase();
            gender = Member.Gender.valueOfIgnoreCase( genderName );
        }

        String kingsChat;
        if  ( ( ( CheckBox ) findViewById(R.id.reg_is_king) ).isChecked() )
            kingsChat = phone;
        else
            kingsChat = ( (TextInputEditText) findViewById(R.id.regs_kings_chat) ).getText().toString();

        if ( first.isEmpty() && last.isEmpty() )
        {
            ( (TextInputEditText) findViewById(R.id.regs_first_name) ).setError("You Must Have A First Name");
            ( (TextInputEditText) findViewById(R.id.regs_last_name) ).setError("You Must Have A Surname");
            return;
        }

        Log.d("Register_Member", "Registering a new member" );

        if ( member == null )
            member = new Member( first, last, other, gender, phone, kingsChat, email, dob, address, selectedCell, selectedRank );
        else
            member.update( first, last, other, gender, phone, kingsChat, email, dob, address, selectedCell, selectedRank  );

        Log.d("Register_Member", "Registering a new member " + member );

        if ( getIntent().getAction() != null && getIntent().getAction().equals(EDIT_MEMBER) )
            edit();
        else
            register();
    }



    private void edit() {
        Call<ModelResponse<Member>> memberCall = ceApi.memberUpdate(member);
        memberCall.enqueue(new Callback<ModelResponse<Member>>() {
            @Override
            public void onResponse(Call<ModelResponse<Member>> call, Response<ModelResponse<Member>> response) {
                if ( response.isSuccessful() )
                {
                    ModelResponse<Member> result = response.body();
                    if (result != null && result.success)
                    {
                        String msg = result.message;
                        Helper.toast( RegisterMemberActivity.this, msg );

                        Intent intent = new Intent();
                        intent.putExtra( "member", result.result.toGson() );
                        setResult( RESULT_OK, intent );
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelResponse<Member>> call, Throwable t) {

            }
        });
    }

    private void notifyExistence() {
        String msg;
        if ( exist )
        {
            msg = "A Member Exist already with the similar credentials";
            Helper.toast( RegisterMemberActivity.this, msg );
        }
        else
        {
            msg = "Ok, your credentials is looking good";
            Helper.toast( RegisterMemberActivity.this, msg );
        }
    }

    private void register() {
        Call<RegisteredMember> memberCall = ceApi.memberRegister(member);
        memberCall.enqueue(new Callback<RegisteredMember>() {
            @Override
            public void onResponse(Call<RegisteredMember> call, Response<RegisteredMember> response) {
                if ( response.isSuccessful() )
                {
                    RegisteredMember registration = response.body();
                    Log.d("Register_Member", "Register Member " + registration );
                    if ( registration != null && !registration.existence )
                    {
                        String msg = "Successfully Register Member";
                        Helper.toast( RegisterMemberActivity.this, msg );

                        Intent intent = new Intent();
                        intent.putExtra( "member", registration.member.toGson() );
                        setResult( RESULT_OK, intent );
                        finish();
                    }
                    else
                    {
                        notifyValidate( registration );
                        String msg = "A Member Exist with this number already";
                        Helper.toast( RegisterMemberActivity.this, msg );
                    }

                }
            }

            @Override
            public void onFailure(Call<RegisteredMember> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( member != null )
        {
            Intent intent = new Intent();
            intent.putExtra( "member", member.toGson() );
            intent.setAction( REG_FAIL );
            setResult( RESULT_OK, intent );
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ifMemberExist() {
        String first = ( (TextInputEditText) findViewById(R.id.regs_first_name) ).getText().toString();
        String sur = ( (TextInputEditText) findViewById(R.id.regs_last_name) ).getText().toString();
        String other = ( (TextInputEditText) findViewById(R.id.regs_other_names) ).getText().toString();
        String phone = ( (TextInputEditText) findViewById(R.id.regs_phone) ).getText().toString();
        Call<ExistanceInfo> validatorCall = ceApi.isMemberExisting(first, sur, other, phone);
        validatorCall.enqueue(new Callback<ExistanceInfo>() {
            @Override
            public void onResponse(Call<ExistanceInfo> call, Response<ExistanceInfo> response) {
                if ( response.isSuccessful() )
                {
                    ExistanceInfo validator = response.body();
                    validate( validator );
                }
            }

            @Override
            public void onFailure(Call<ExistanceInfo> call, Throwable t) {

            }
        });
    }

    private void validate(ExistanceInfo validator) {
        boolean existence = report(validator);
        if ( exist != existence )
        {
            exist = existence;
            notifyExistence();
        }
    }

    private void notifyValidate(ExistanceInfo validator) {
        boolean existence = report(validator);
        exist = existence;
        notifyExistence();
    }

    private boolean report(ExistanceInfo validator) {
        boolean existence = validator.isExistence();
        if (existence)
        {
            if  ( validator.firstName )
                ( (TextInputEditText) findViewById(R.id.regs_first_name) ).setError("Someone is registered with same first name");
            if  ( validator.surname )
                ( (TextInputEditText) findViewById(R.id.regs_last_name) ).setError("Someone is registered with same surname name");
            if  ( validator.otherNames )
                ( (TextInputEditText) findViewById(R.id.regs_other_names) ).setError("Someone is registered with same other names");
            if  ( validator.phoneNumber )
                ( (TextInputEditText) findViewById(R.id.regs_phone) ).setError("Someone is registered with same phone number");
//            ((FloatingActionButton) findViewById(R.id.reg_action)).hide();
        }
        else
        {
            ( (TextInputEditText) findViewById(R.id.regs_first_name) ).setError(null);
            ( (TextInputEditText) findViewById(R.id.regs_last_name) ).setError(null);
            ( (TextInputEditText) findViewById(R.id.regs_other_names) ).setError(null);
            ( (TextInputEditText) findViewById(R.id.regs_phone) ).setError(null);
//            ((FloatingActionButton) findViewById(R.id.reg_action)).show();
        }
        return existence;
    }

    public class NameValidator implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ifMemberExist();
        }
    }
}

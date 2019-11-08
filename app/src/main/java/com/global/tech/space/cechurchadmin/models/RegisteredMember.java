package com.global.tech.space.cechurchadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisteredMember extends ExistanceInfo<RegisteredMember>
{
    @SerializedName("member")
    @Expose
    public Member member;

    public final static RegisteredMember creator = new RegisteredMember();

    public RegisteredMember()
    {
        super(RegisteredMember.class);
    }
}

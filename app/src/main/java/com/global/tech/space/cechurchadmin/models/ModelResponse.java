package com.global.tech.space.cechurchadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelResponse<T extends Model> extends Info<ModelResponse>
{
    @SerializedName("success")
    @Expose
    public boolean success;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("result")
    @Expose
    public T result;

    public final static ModelResponse creator = new ModelResponse();

    public ModelResponse() {
        super(ModelResponse.class);
    }
}

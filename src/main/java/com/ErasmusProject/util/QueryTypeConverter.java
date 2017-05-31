package com.ErasmusProject.util;

import java.beans.PropertyEditorSupport;

/**
 * Created by Komp on 16.2.2017.
 */
public class QueryTypeConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        String capitalized = text.toUpperCase();
        QueryType qt = QueryType.valueOf(capitalized);
        setValue(qt);
    }
}

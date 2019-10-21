package com.mikerusoft.euroleague.modelToEntityConvertor;

public interface ConverterI {

    <F, T> T convert(F obj, Class<T> classTo);

}

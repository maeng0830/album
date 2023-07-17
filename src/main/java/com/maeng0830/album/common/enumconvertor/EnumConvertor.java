package com.maeng0830.album.common.enumconvertor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public abstract class EnumConvertor<T extends Enum<T> & EnumType> implements AttributeConverter<T, String> {

	private Class<T> enumClass;


	public EnumConvertor(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public String convertToDatabaseColumn(T enumValue) {
		if (enumValue == null) {
			return null;
		} else {
			return EnumConvertorUtil.toCode(enumValue);
		}
	}

	@Override
	public T convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		} else {
			return EnumConvertorUtil.toValue(enumClass, dbData);
		}
	}
}

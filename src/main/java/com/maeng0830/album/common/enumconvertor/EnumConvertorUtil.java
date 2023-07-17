package com.maeng0830.album.common.enumconvertor;

import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumConvertorUtil {

	public static <T extends Enum<T> & EnumType> T toValue(Class<T> enumClass, String code) {
		if (code == null) {
			return null;
		} else {
			return EnumSet.allOf(enumClass).stream()
					.filter(v -> v.getCode().equals(code))
					.findAny()
					.orElseThrow(() -> new IllegalArgumentException(
							String.format("enum=[%s], legacyCode=[%s]가 존재하지 않습니다.",
									enumClass.getName(), code)));
		}
	}

	public static <T extends Enum<T> & EnumType> String toCode(T enumValue) {
		if (enumValue == null) {
			return null;
		}
		return enumValue.getCode();
	}
}

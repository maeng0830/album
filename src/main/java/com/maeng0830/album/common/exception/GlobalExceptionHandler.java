package com.maeng0830.album.common.exception;

import com.maeng0830.album.common.exception.bindexception.BindExceptionResponse;
import com.maeng0830.album.common.exception.code.ApiExceptionCode;
import com.maeng0830.album.common.exception.commonexception.CommonExceptionResponse;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	@ExceptionHandler(AlbumException.class)
	public ExceptionResponse albumExceptionHandler(AlbumException e) {
		return new ExceptionResponse(e.getCode(), e.getMessage());
	}

	// IllegalArgumentException
	@ExceptionHandler(IllegalArgumentException.class)
	public CommonExceptionResponse illegalArgumentException(IllegalArgumentException e) {
		return new CommonExceptionResponse(e);
	}

	// json -> 객체 생성 오류(type miss match 오류)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ExceptionResponse httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
		try {
			throw  new AlbumException(ApiExceptionCode.NOT_READABLE, e);
		} catch (AlbumException albumException) {
			return new ExceptionResponse(albumException.getCode(), albumException.getMessage());
		}
	}

	// json -> 객체 바인딩 오류(validation 오류)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	public List<BindExceptionResponse> bindException(BindException e) {
		return e.getBindingResult().getAllErrors()
				.stream()
				.map(error -> new BindExceptionResponse(error.getCode(), messageSource.getMessage(error, Locale.KOREA)))
				.collect(Collectors.toList());
	}
}

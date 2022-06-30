package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EXISTS_NAME(false, 2018, "중복된 닉네임입니다."),
    POST_USERS_EMPTY_PASSWORD(false, 2019, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_BORN_YEAR(false, 2020,"출생년도를 확인해주세요."),
    POST_USERS_EMPTY_BORN_YEAR(false, 2021, "출생년도를 입력해주세요."),
    POST_USERS_EMPTY_NAME(false, 2022, "닉네임을 입력해주세요."),
    POST_USERS_EMPTY_GENDER(false, 2023, "성별을 입력해주세요."),
    POST_USERS_SHORT_PASSWORD(false, 2027, "비밀번호가 너무 짧습니다."),

    // Common : id형식이 long이어야하는데 String형이 들어왔을 경우/ long의 범위를 넘어갈 경우
    INVALID_ID(false, 2024, "올바르지 않은 id 형식입니다."),

    // [POST] /users/log-in
    POST_LOGIN_CHECK_EMAIL_OR_PW(false, 2025, "이메일이나 패스워드를 확인해주세요."),

    // [PATCH] /users/:userId
    PATCH_PASSWORD_EMPTY(false, 2026, "변경할 비밀번호를 입력하세요."),
    PATCH_PASSWORD_SHORT(false, 2028, "변경할 비밀번호가 너무 짧습니다."),

    // [GET] /app/categories?category-id=:categoryId
    EMPTY_CATEGORY_ID(false, 2029, "카테고리가 비어있습니다."),
    GET_CATEGORY_NOT_EXISTS(false, 2030, "없는 카테고리입니다."),

    // COMMON
    EMPTY_PATH_VARIABLE(false, 2031, "path variable이 비어있습니다."),

    // [GET] /app/events/:eventId
    EVENT_NOT_EXISTS(false, 2032, "존재하지 않는 이벤트입니다."),

    // COMMON
    MINI_CATEGORY_NOT_EXISTS(false, 2033, "존재하지 않는 하위 카테고리입니다."),
    TYPE_ERROR_NOT_BOOLEAN(false, 2034, "쿼리 파라미터는 boolean형이 들어와야합니다."),
    INVALID_ITEM_NUMBER(false, 2035, "구매할 제품의 개수는 0개거나 그 이하일 수 없습니다."),

    EMPTY_OPTION_NUMBER(false, 2036, "구매할 상품의 개수를 입력해주세요."),
    EMPTY_OPTION_ID(false, 2037, "구매할 상품을 선택해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    USER_NOT_EXISTS(false, 3015, "존재하지 않는 유저입니다."),
    ITEM_ID_NOT_EXISTS(false, 3016, "존재하지 않는 상품 id입니다."),
    NOT_VALID_OPTION(false, 3017, "해당 상품에 속하지 않는 옵션입니다."),
    OPTION_ID_NOT_EXISTS(false, 3018, "존재하지 않는 옵션 id입니다."),
    INVALID_ITEM_OPTION(false, 3019, "올바르지 않은 상품 옵션입니다."),
    KART_ITEM_ALREADY_EXISTS(false, 3020, "장바구니에 이미 담겨져있습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    UPDATE_FAIL_PASSWORD(false, 4015, "유저 비밀번호 변경 실패");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

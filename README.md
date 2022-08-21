# 💒 오늘의 집 모의외주 프로젝트
## 💻 Tech Stack
- Back-end

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"> <img src="https://img.shields.io/badge/NginX-009639?style=for-the-badge&logo=NGINX&logoColor=white">

## 📜 ERD
👉 [ERD로 바로가기](https://aquerytool.com/aquerymain/index/?rurl=396ea193-ceab-4631-9fb7-a89881e24ad1)

👉 ERD 비밀번호 : t2668t

## 📚 API 명세서
👉 [API 명세서 바로가기](https://docs.google.com/spreadsheets/d/17xysIzGC0r-gmkm_j8YGMpJk9vXouF4FdzwGY2p8GSk/edit?usp=sharing)

## 🛠 개발 일지
### 1️⃣ 1일차 [2022-06-25] 진행 상황
- [기획서 제출](https://docs.google.com/document/d/1peAyH_VD2uvIPDvj1zteVj-e37PzgSWshk9y-FddoGY/edit?usp=sharing) - 100%
- [도메인](https://rc-rising-test-6th.shop) 적용(도메인 : rc-rising-test-6th.shop) - 100%
- dev, prod 서브 도메인 적용 - 100%
- let's encrypt를 사용한 SSL 적용(서브 도메인까지 모두 적용) - 100%
- EC2 인스턴스 생성 / Elastic IP 적용 - 100%
- RDS 생성 및 Datagrip으로 외부에서 접속 - 100%
- 개발을 위한 JPA 기본 틀 업로드 / 테스트 API 적용 완료 - 100%

### 2️⃣ 2일차 [2022-06-26] 진행 상황
- ERD 1차 설계
![오늘의_집_모의외주_프로젝트_20220626_123333](https://user-images.githubusercontent.com/47571973/175799127-263e05e1-550e-41e2-a6dc-d6dcb3a0ed35.png)
- API 일부 구현 및 [API 명세서](https://docs.google.com/spreadsheets/d/17xysIzGC0r-gmkm_j8YGMpJk9vXouF4FdzwGY2p8GSk/edit?usp=sharing) 업데이트
- [더미 데이터](https://www.notion.so/softsquared/e967108f6b34437d9a1368212f10a7d1) 일부 제작
- 회원가입 API, 로그인 API, 비밀번호 변경 API들을 만들었다. 

### 3️⃣ 3일차 [2022-06-27] 진행 상황
- 이벤트 배너를 가져오는 API 작성
- 카테고리 분류 불러오는 API 작성
- 이벤트 상세페이지 불러오는 API 작성
- URL Validation을 위한 함수 추가
- 이미지 업로드를 위한 AWS S3 틀 작성
- 테이블 추가(MiniCategories, EventImgs)

### 4️⃣ 4일차 [2022-06-28] 진행 상황
- 메인 화면 불러오는 API 추가
- 이벤트 상세 페이지 API 추가
- API Response에서 비효율적/가독성 떨어지는 부분 수정

### 5️⃣ 5일차 [2022-06-29] 진행 상황
- ERD 일부 수정
    - 카테고리 계층이 하나 더 있다는 걸 설계하면서 발견하여 카테고리 계층 하나 더 추가하였다
      - `SmallestCategories` 테이블 추가
    - Items 컬럼 중에 `isMDPick`이라는 컬럼을 추가하였다.
      - API 설계 중 나머지는 다 같은데 MD Pick인지 여부에 따라 UI에 표시되는 위치가 달랐다. 따라서 MD Pick 아이템인지 여부를 확인하는 컬럼이 필요하여 이를 추가하였다.
- 미니 카테고리 관련 세부 페이지 불러오는 API 추가
- 마이페이지, 나의 쇼핑 페이지, 내가 작성한 리뷰 목록 불러오는 API 작성

### 6️⃣ 6일차[2022-06-30] 진행 상황
- 회원가입 시 닉네임 길이 validation 추가
- 스토어 홈 API에서 핫딜 썸네일 누락 해결
- 상품 상세페이지 조회 API 추가
- 장바구니 담기를 위한 옵션 조회 API 추가
- 장바구니에 상품 옵션 추가하는 API 추가
- 장바구니 조회 API 추가
- 장바구니에 담겨있는 상품 옵션 변경 API 추가

### 7️⃣ 7일차[2022-07-01] 진행 상황
- 쿠폰 조회 API 작성
- 쿠폰 받기 API 작성
- 스크랩 폴더 만들기 API 작성
- 상품 스크랩하기 API 작성
- 상품 상세페이지 API 변경 : 스크랩 여부 확인할 수 있도록 함

### 8️⃣ 8일차[2022-07-02] 진행 상황
- 스크랩 목록 조회 API 생성
- 스크랩 목록 조회 API 제작 중 ERD 변경
  - 스크랩한 상품들과 스크랩한 커뮤니티 글들을 구분할 수가 없었다.(모두 PK가 long형이어서 따로 구분하기 힘들었다.)
  - 이 때문에 테이블을 하나 더 생성하였다.
- 스토어 메인 API과 상품 상세 보기에서 할인률 계산이 잘못되어 있는 것을 수정
  - 100*(price-saledPrice)/price가 할인률인데 100*saledPrice/price라고 되어 있어서 이를 수정하였다.
- 지금 제작 중인 주문 생성 API를 만드는 도중 장바구니 조회 API에서 장바구니의 기본 키가 필요한 것을 발견
    - 장바구니 조회 API에 인자를 추가하였다.

### 9️⃣ 9일차[2022-07-03] 진행 상황
- 주문 생성 API 작성 및 리팩토링
- 유저 회원가입 시 기본 프로필 사진 url 등록
- 주문 화면 불러오기 API 작성

### 🔟 10일차[2022-07-04] 진행 상황
- 카카오톡 oAuth API 제작
- 리뷰 작성 API 제작
  - 이 과정에서 AWS S3 연결 오류가 발생하여 이미지 업로드는 생략하였다.
- 리뷰 작성 화면 조회 API 제작
- 쿠폰 등록 API 제작
  - 이 과정에서 쿠폰 코드들을 저장하는 테이블이 필요하여 ERD를 수정하였다.
    - `CouponCodes`라는 테이블을 추가하여 쿠폰 코드들을 저장해두었고 해당 코드들의 status로 사용자 등록 여부를 확인하였다.

### 1️⃣1️⃣ 11일차[2022-07-05] 진행 상황
- Inquiry에서 상품 옵션 별로 문의를 넣을 수 있으므로 optionId로 테이블 설계를 바꾸었다. itemId에서 optionId로 바꾸었으므로 key constraint도 바꾸었고, 쿼리문도 일부 수정하였다.
  - 커밋 기록: [4db7816](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/4db78165a9d2c9afd4e1ef3671ff92a28363aa38)
- 상품 옵션 문의 조회 API를 추가
  - 커밋 기록 : [d5a9b59](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/d5a9b595782f3dcc0f4bb6862de37141f98e0454)
- 상품 문의 생성 API 추가
  - 커밋 기록 : [513fce0](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/513fce05dccf03ad9778a31fe48d14a316ffc162)
- 유저 좋아요 목록 API 추가
  - 커밋 기록 : [dbbb23a](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/513fce05dccf03ad9778a31fe48d14a316ffc162)
- 팔로잉/팔로워 목록 조회 API 추가
  - 커밋 기록 : [739ffd7](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/739ffd7ae5b415cbc9316d33dd2d8dee37b78236), [a377a9a](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/a377a9a029f6e13e88c48705287c2f363b28ee50)

### 1️⃣2️⃣ 12일차[2022-07-06] 진행 상황
- 리뷰 확인 API 수정
  - 이미지는 최대 1개가 들어갈 수 있으므로 List형이 아니라 String형으로 바꿔달라는 클라이언트 개발자 분의 요청에 따라 수정
  - 리뷰 관련 API를 전부 수정하였다.
  - 커밋 기록: [55ac42f](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/55ac42f400896914d4bfd065f1e900878eb19b6b)
- 장바구니 담기 API 수정
  - 클라이어트 개발자 분이 장바구니 담기 API를 엮는 도중 validation이 이상하다는 말씀을 하셨다.
  - 실제로, 이미 장바구니에 담긴 상품을 장바구니 또 담으려고 하면 `이미 장바구니에 담겨져 있는 상품입니다.`라는 에러가 떠야하는데 `데이터베이스 연결에 실패하였습니다.`라는 에러가 떴다.
  - 따라서 이 부분을 수정하였다.
  - 커밋 기록: [548c7ac](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/548c7ac6bb629c2e681f9d4ea4022395326d8994)
- 비회원 주문 조회 API 생성
  - 유일한 비회원 API인 비회원 주문 조회 API를 생성했다.
  - 커밋 기록 : [20a3006](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/20a300630462ef76de975b6559aaee731538cf8c)

### 1️⃣3️⃣ 13일차[2022-07-07] 진행 상황
- 이메일 인증을 위한 이메일 전송 API와 이메일 코드 확인 API 작성
  - 커밋 기록: [3605921](https://github.com/mock-rc6/today-s_house_B_server_daisy/commit/36059212f23d367bf6acb23b2cdc29016c560f36)

---
본 템플릿의 저작권은 (주)소프트스퀘어드에 있습니다. 상업적 용도의 사용을 금합니다
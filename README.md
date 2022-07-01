# 💒 오늘의 집 모의외주 프로젝트
## 💻 Tech Stack
- Back-end


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
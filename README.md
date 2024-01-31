**Project: fincette-crawler**
================================
- 개발환경
  - language: JAVA1.8
  - 의존성관리: maven
  - (운영)API Server: https://api.nuzal.kr
  
- 빌드 & 배포
  - Apache ant
  - jar 빌드
    - 빌드 실행: ant -buildfile 빌드파일 target명
      - 예) ant -buildfile ABL_build.xml create_run_jar
  - github action 
    - workflow: 크롤링jar CI 
    - 회사단위로 빌드 및 API서버에 업로드
      
- 배포
  - Crawler(Monitoring) PC 접속방법: 원격데스크탑 연결

     - 네이버클라우드플랫폼 Windowns server 2016
       - host: 103.244.108.224/103.244.108.94
       - id: cr001~cr018
       - pw: !@웰그램12
       - 배포시 주의사항: 최초는 최신 소스로 업데이트 후 배포할 것(hostname=>username)
  
- 실행
  - 사전준비 
    - c:\crawler 디렉터리 설정 driver, extentens 디렉터리
    - mudfish 계정등록(컴퓨터이름)
  - crawler jar 다운로드
      - 예1) 전체다운로드: java -jar downloadEx.jar
      - 예2) 회사별 다운로드: java -jar downloadEx.jar -p=회사코드, 
        - (java -jar downloadEx.jar -p=ABL, java -jar downloadEx.jar -p=SLI, ...)
      - 예3) 해당보험상품만 다운로드: java -jar downloadEx.jar -p=상품코드
        - (java -jar downloadEx.jar -p=ABL_CCR_D001, java -jar downloadEx.jar -p=SLI00029, ...)
        
  - crawler jar 실행
    - 상품jar크롤러ClI Command 사용법 @platform_dev
      : (참고코드: ABL_MDC_D001.java)
      
    - Usage: <상품코드>.jar [-hm] [-n=<vpn>] [-z=<zero>] [-a=<ages>]...
    [-p=<planIdList>]...
    -a, --age=<ages>          보험나이
    -h, --help                display a help message
    -m, --monitoring          monitoring mode
    -n, --vpn=<vpn>           VPN(mudfish) 사용
    -p, planId=<planIdList>   가설ID
    -z, --zero=<zero>         보험료 0원인것만 크롤링
      
    [예제]
  
    ```
    예제1. 모니터링
      - option: -m
        java -jar ABL_MDC_D001.jar -m
        예제2. 상품코드 전체 크롤링(상품코드:ABL_MDC_D001)
      - option: 없음
        java -jar ABL_MDC_D001.jar
        예제3. 상품코드의 가설ID로 크롤링(상품코드:ABL_MDC_D001, 가설ID:1808)
      - option: -p
        java -jar ABL_MDC_D001.jar -p=18008
        예제5. 상품코드의 가설ID과 나이로 크롤링하기(상품코드:ABL_MDC_D001, 가설ID:1808, 나이:19,20,21,22,30)
      - option: -a
        java -jar ABL_MDC_D001.jar -p=18008 -a=19 -a=20 -a=21 -a=22 -a=30
        예제6. 상품코드의 모든 특약의 납입보험료가 빈값이거나 "0"이면  크롤링하기(상품코드:ABL_MDC_D001, 가설ID:1808, 나이:19,20,21,22,30)
      - option: -z=1
        java -jar ABL_MDC_D001.jar -z=1
        예제7. 모든 특약의 납입보험료와 만기 해약환급금이 빈값이거나 "0"이면 크롤링하기(상품코드:ABL_MDC_D001, 가설ID:1808, 나이:19,20,21,22,30)
      - option: -z=2
        java -jar ABL_MDC_D001.jar -z=2
        예제8. 모니터링 또는 크롤링 시에, vpn 연결하기
      - option: -n
        java -jar ABL_MDC_D001.jar -n=mudfish
        java -jar ABL_MDC_D001.jar -m -n=mudfish  
    ```
    
- 외부 property파일 참조
    - 기본적으로, jar 파일내부의 crawler-properties.xml을 참조한다.
    하지만, jar 파일의 같은 경로에 crawler-properties.xml 을 만들어주면, 그 파일을 참조하도록 한다.

- 시스템 사용자와 크롤링 담당자간 GIT정책, CODE_CONVENTION 참조 (2023.11.29)
  - GIT & CODE_CONVENTION : https://www.notion.so/GIT-ff5d2c80521c4d4b9ae3a14333776c0e?pvs=4
    

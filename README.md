# ft_hangouts

<center>
  <img width="309" alt="first_screen" src="https://github.com/tjdwns5063/ft_hangouts/assets/73011361/faaf40e1-1622-45fd-8daa-4cc20225f4b8">
</center>


ft_hangouts은 연락처 앱의 기본적인 기능들을 구현한 안드로이드 애플리케이션입니다.

서드파티 라이브러리를 사용하지 않고 최대한 저수준 라이브러리를 사용하여 개발하려고 했습니다.

## 핵심 기능

- 연락처 CRUD
- 연락처 검색
- 다국어 지원(영어, 한국어)
- 연락처에 바로 통화, 문자 보내기

## 기술 스택

- Android
- Kotlin
- SQLite
- MVVM

## 배운점

- 저수준 라이브러리를 사용하여 개발하고 서드파티 라이브러리를 사용하여 리팩터링.
    - SQLite와 이미지 처리 등을 저수준 라이브러리를 사용해서 진행했습니다. 저수준 라이브러리를 사용하며, 안드로이드에서 많이 사용되는 Glide, Room등의 라이브러리가 어떤 문제를 해결하고 편리하게 만드는지 깨달을 수 있었습니다.
- 다양한 UI 구현 경험
    - `ItemTouchHelper` 와 `RecyclerView` 를 이용해 스와이프 기능 구현
        - 오른쪽, 왼쪽 스와이프에 따라 전화 걸기, 문자 보내기 기능
    - `CoordinatorLayout` 을 이용하여 스크롤 변화에 따라 `AppBar`를 조절하기 위해 `CustomBehavior` 구현
- 유닛 테스트 작성 경험
    - `Coroutine`을 이용한 비동기 메서드를 테스트를 작성 해볼 수 있었습니다.
    - Unit Test를 작성하며 `LiveData`와 `StateFlow` 중 StateFlow가 테스트가 편한 이유를 체험할 수 있었음.
    - `robolectric` 을 사용해서 unit test로 간편하게 안드로이드 의존성 사용할 수 있는 방법 학습

### 프로젝트 깃허브: https://github.com/tjdwns5063/ft_hangouts

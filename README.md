# Secure Crypto Notes

Мобільний додаток для безпечного зберігання нотаток та відстеження криптовалют.  
Створений за допомогою **Jetpack Compose** з підтримкою CoinGecko API.

---

## Опис

Secure Crypto Notes дозволяє:
- зберігати приватні нотатки з локальним шифруванням;
- входити у застосунок за допомогою PIN-коду або біометрії;
- переглядати криптовалюти та додавати їх у "збережене";
- переглядати графіки цін монет за останні 7 днів.

---

## Функціональність
- Авторизація через PIN-код та біометрію
- Шифрована база даних Room
- Додавання та перегляд списку нотаток
- Перегляд списку монет з CoinGecko API
- Графік зміни ціни монети за 7 днів
- Збереження улюблених монет

---

## Технології
- Kotlin + Jetpack Compose
- Hilt (DI)
- Room + SQLCipher
- Retrofit2 + OkHttp
- MPAndroidChart
- Coroutines + Flow
- MVVM + Clean Architecture

---

## Як запустити проєкт

1. **Клонувати репозиторій**
   ```bash
   git clone https://github.com/vishenkaaa/Secure-crypto-notes.git
   cd secure-crypto-notes
   ```

2. **Налаштувати API ключ**

- Зареєструйтесь у CoinGecko
- Створіть у корені проєкту файл .env і додайте ключ:
```properties
COINGECKO_API_KEY=your_api_key_here
```

3. **Запустити застосунок**

- В Android Studio натисніть ▶ Run
- або через термінал:

```bash 
./gradlew installDebug
```
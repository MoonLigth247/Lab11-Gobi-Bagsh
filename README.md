# Lab 11: Mutation Testing with PIT

## Оюутны мэдээлэл

- **Овог нэр:** Н.Мөнхбаяр

- **Оюутны код:** B242270058

- **java -version:** 

debain13@debian:~/Lab11-Gobi-Bagsh/starter/lab11-pit$ java -version

openjdk version "21.0.13" 2026-10-20

OpenJDK Runtime Environment (build 21.0.13+9-1-deb13u2-Debian)

OpenJDK 64-Bit Server VM (build 21.0.13+9-1-deb13u2-Debian, mixed mode, sharing)

- **mvn -version:**

debain13@debian:~/Lab11-Gobi-Bagsh/starter/lab11-pit$ mvn -version

Apache Maven 3.9.9

Maven home: /usr/share/maven

Java version: 17.0.21, vendor: Eclipse Adoptium, runtime: /usr/lib/jvm/temurin-17-jdk-amd64

Default locale: en_US, platform encoding: UTF-8

OS name: "linux", version: "6.12.117+deb13-amd64", arch: "amd64", family: "unix"



## 1. Эхний хэмжилт ба зөрүүний тайлбар

### JaCoCo (Line Coverage)
- **Line Coverage:** 100% (25/25)
- **Branch Coverage:** 38/41
- `results/jacoco-start.csv` файлаас үзэхэд `LINE_MISSED=0`, `LINE_COVERED=25` байна.

### PIT (Mutation Coverage)
- **Generated:** 63 mutations
- **Killed:** 17 (27%)
- **Test Strength:** 27%
- Гаралтын файл: `results/pit-start.txt`

### Зөрүүний тайлбар
Line coverage 100% байсан ч mutation coverage ердөө **27%** байсан нь тестийн багцын assertion-ууд хэт сул байгааг харуулж байна. Анхны тестүүд нь зөвхөн `assertNotNull`, `assertTrue(муж)` зэрэг сул шалгалтууд хийсэн тул кодын мөрүүд гүйцэтгэгдэж байгаа ч, мутацилагдсан (өөрчлөгдсөн) кодын буруу зан төлөвийг илрүүлж чадахгүй байна. Хамралт (coverage) нь зөвхөн "код гүйцэтгэгдсэн эсэх"-ийг хэмждэг бол, mutation score нь "тестүүд алдааг илрүүлж чадах эсэх"-ийг хэмждэг.



## 2. Хэсэг A: Амьд үлдсэн (SURVIVED) мутантуудын ангилал

`pit-start.txt` файлын дагуу анхны хэмжилтэд 46 мутант амьд үлдсэн. Гол мутацийн төрлүүдийг доорх хүснэгтээр ангилав.

| Мөр | Мутаци (PIT-ийн тайлбар) | Ангилал | Арга хэмжээ |
| :--- | :--- | :--- | :--- |
| 26, 27, 28, 29 | `changed conditional boundary` (>= → >) | Тест дутуу | 90/89.99, 80/79.99, 70/69.99, 60/59.99 зааг утгын тестүүд нэмнэ |
| 35 | `replaced return value` ("A" → "B" эсвэл хоосон) | Тест дутуу | `assertEquals("A", ...)` ашиглан яг утгыг шалгана |
| 36, 37, 38, 39 | `replaced primitive return value` (4, 3, 2, 1 → 0) | Тест дутуу | gpa-ийн switch бүрийн хувьд `assertEquals(4.0, ...)` гэх мэт яг утга шалгана |
| 50 | `removed conditional - replaced equality check with false` | Тест дутуу | `gpa=0`, `gpa=4`, `credits=0` зэрэг оролтын хязгаарын тест нэмнэ |
| 51, 52 | `changed conditional boundary` (>= → >) | Тест дутуу | scholarship-ийн 3.8/15, 3.4/12, 3.0 зааг утгуудыг шалгана |
| 53 | `replaced int return value` (100 → 0) | Тест дутуу | `assertEquals(100, calc.scholarship(3.8, 15, false))` нэмнэ |
| 54 | `removed call to hasViolation` | Тест дутуу | `hasViolation=true` үед 0 буцаахыг шалгана |

**Ангиллын үндэслэл:**
- **Тест дутуу:** Дээрх бүх өөрчлөлтүүд нь програмын бодит зан төлөвийг өөрчилдөг (жишээ нь, 90 оноотой хүнд "B" дүн өгөх, эсвэл тэтгэлэг 100 байх ёстой үед 0 өгөх). Анхны тестүүд нь зөвхөн "муж дотор байна уу" гэж шалгадаг байсан тул эдгээрийг барьж чадаагүй.
- **Equivalent:** Анхны хэмжилтэд бүрэн equivalent гэж батлагдсан мутант байхгүй (эсвэл PIT-ийн built-in filter-үүд аль хэдийн хассан). Бүх амьд үлдсэн мутант нь "Тест дутуу" гэж ангилагдав.

---

## 3. Хэсэг B: Тестүүдийг хүчтэй болгох явц

### B1: Assertion-уудыг яг утга болгох
`assertNotNull`, `assertTrue(муж)` шалгалтуудыг `assertEquals` болгож өөрчилсөн.
- `letterGrade(95)` → `assertEquals("A", ...)`
- `gpa({95, 85})` → `assertEquals(3.5, ...)`
- `scholarship(3.9, 16, false)` → `assertEquals(100, ...)`

**B1-ийн дараах хэмжилт (`results/pit-B1.txt`):**
- **Generated:** 63 mutations
- **Killed:** 50 (79%)
- **Test Strength:** 79%
- *Ажиглалт:* Assertion-уудыг яг утга болгосноор ихэнх `return value` мутантууд аглагдсан. Гэвч `changed conditional boundary` мутантууд (ConditionalsBoundaryMutator: 9 survived) болон `removed conditional` мутантууд (RemoveConditionalMutator_ORDER_IF: 4 survived) хэвээр үлдсэн.

### B2: Зааг утгын тестүүд нэмэх
Дараах зааг утгуудад зориулсан тестүүдийг нэмсэн:
- `letterGrade`: 90, 89.99, 80, 79.99, 70, 69.99, 60, 59.99, 0, 100
- `gpa`: A, B, C, D, F бүх үсгэн дүнг тус тусад нь болон холимог байдлаар шалгах
- `scholarship`: (3.8, 15), (3.79, 15), (3.8, 14), (3.4, 12), (3.39, 12), (3.4, 11), (3.0, 0), (2.99, 100), (4.0, 15), (0.0, 0)

**B2-ийн дараах эцсийн хэмжилт (`results/pit-B2.txt`):**
- **Generated:** 63 mutations
- **Killed:** 63 (100%)
- **Test Strength:** 100%
- *Ажиглалт:* Зааг утгын тестүүд нэмснээр бүх `ConditionalsBoundaryMutator`, `RemoveConditionalMutator` мутантууд амжилттай аглагдсан. Мутацийн хамралт 100% болсон.

### Хэмжилтийн хураангуй хүснэгт
| Хэмжилт | Файл | Generated | Killed | Mutation Coverage | Test Strength |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Эхлэл | `results/pit-start.txt` | 63 | 17 | 27% | 27% |
| B1 дараа | `results/pit-B1.txt` | 63 | 50 | 79% | 79% |
| B2 дараа (эцсийн) | `results/pit-B2.txt` | 63 | 63 | **100%** | **100%** |

---

## 4. Хэсэг C: Гар мутацийн туршилт ба Equivalent мутант

### 4.1 Гар мутацийн туршилт
- **Мутаци:** `ScholarshipCalculator.java`-ийн 51-р мөрөнд `if (gpa >= 3.8 && credits >= 15)` гэснийг `if (gpa > 3.8 && credits >= 15)` болгож өөрчилсөн.
- **Үр дүн (`results/mvn-test-manual-mutant.txt`):**
 ScholarshipCalculatorTest.scholarship_boundaryValues:80 expected: <100> but was: <70>
scholarship_boundaryValues тест унасан. Учир нь gpa=3.8, credits=15 үед 100 буцаах ёстой байсан ч, мутацилагдсан код 70 буцаасан.

## Дүгнэлт: B2-ийн зааг утгын тест нь гараар хийсэн мутантыг амжилттай барьж (kill) чадсан. Дараа нь git restore src/main ашиглан мутацийг буцаасан.

## 4.2 Өөрийн Equivalent мутант
Мутант: gpa() метод дахь total += 4; гэснийг total += (4 * 1); болгож өөрчлөх (эсвэл letterGrade дахь score >= 90-ийг score >= 90.0 болгох).

Тайлбар: Энэхүү өөрчлөлт нь програмын зан төлөвийг огт өөрчлөхгүй, учир нь 4 ба 4 * 1 нь математикийн хувьд ижил утгатай. Иймд ямар ч оролт өгөхөд ижил хариу буцаах тул ямар ч тест энэ мутантыг барьж чадахгүй. Ийм мутантыг зөвхөн гараар "Equivalent" гэж ангилж болно.

## 5. Дүгнэлт
Энэхүү лабораторийн ажлаар хамралт (coverage) болон мутацийн оноо (mutation score) нь тестийн чанарыг хэмжих хоёр өөр үзүүлэлт гэдгийг практикаар батлав. Эхний тестийн багц JaCoCo-гоор 100% хамралттай байсан ч, PIT-ээр ердөө 27% мутацийн оноотой байсан нь тестүүд код гүйцэтгэдэг ч, логик алдааг илрүүлж чадахгүй байгааг харуулсан. Assertion-уудыг assertEquals болгож хүчтэй болгосноор (B1) оноо 79% болж өссөн. Харин зааг утгын тестүүдийг (B2) нэмснээр 100% мутацийн оноотой болж, тестийн багц маань үнэхээр чанартай болсныг батлав. AI-гаар тест бичүүлэхэд ч мөн адил AI нь ихэвчлэн зөвхөн "happy path" буюу хэвийн үед ажиллах тестүүд бичдэг бөгөөд зааг утга, алдааны нөхцөлийг орхигдуулдаг тул зөвхөн coverage-оор бус, mutation score-оор заавал шалгах шаардлагатай байдаг.

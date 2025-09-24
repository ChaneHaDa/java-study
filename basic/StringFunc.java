public class StringFunc {
    public static void main(String[] args) {
        // 문자열 리터럴 방식과 문자열 객체 방식의 차이

        // 문자열 리터럴
        String str1 = "Hello, World!";
        System.out.println(str1.length());
        System.out.println(str1.toUpperCase());
        System.out.println(str1.toLowerCase());
        System.out.println(str1.substring(0, 5));
        System.out.println(str1.substring(6));
        System.out.println(str1.substring(6, 11));

        // 문자열 객체
        String str2 = new String("Hello, World!");
        System.out.println(str2);
        System.out.println(str2 == str1);
        System.out.println(str2.equals(str1));

        // 문자열 리터럴
        String str3 = "Hello, World!";
        System.out.println(str3);
        System.out.println(str3 == str1);
        System.out.println(str3.equals(str1));

        System.out.println(System.identityHashCode(str1));
        System.out.println(System.identityHashCode(str2));
        System.out.println(System.identityHashCode(str3));

        System.out.println("\n=== StringBuilder vs String 성능 비교 ===");

        /*
         * String이 불변(immutable)인 이유와 문제점:
         * - String은 한번 생성되면 내용을 변경할 수 없음
         * - "a" + "b" 연산 시 새로운 String 객체가 생성됨
         * - 반복적인 문자열 연결 시 메모리 낭비와 성능 저하 발생
         * - 가비지 컬렉션의 부담 증가
         */

        // String 연결 - 비효율적 (매번 새로운 String 객체 생성)
        System.out.println("String 연결 방식 테스트 중...");
        long startTime = System.currentTimeMillis();
        String result1 = "";
        for (int i = 0; i < 10000; i++) {
            // 매 반복마다 새로운 String 객체가 힙 메모리에 생성됨
            // 기존 result1은 가비지가 되어 GC 대상이 됨
            result1 += "a";  // 내부적으로 new String(result1 + "a") 와 같음
        }
        long endTime = System.currentTimeMillis();
        System.out.println("String 연결 시간: " + (endTime - startTime) + "ms");
        System.out.println("최종 문자열 길이: " + result1.length());

        /*
         * StringBuilder의 동작 원리:
         * - 내부적으로 가변 크기의 char 배열 사용
         * - append() 호출 시 배열에 직접 문자 추가
         * - 배열 크기가 부족하면 자동으로 확장 (보통 2배)
         * - toString() 호출 시에만 최종 String 객체 생성
         */

        // StringBuilder 사용 - 효율적 (내부 배열에 직접 추가)
        System.out.println("StringBuilder 방식 테스트 중...");
        startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(); // 기본 용량 16으로 시작
        for (int i = 0; i < 10000; i++) {
            // 내부 char 배열에 직접 문자 추가, 객체 생성 없음
            sb.append("a");
        }
        String result2 = sb.toString(); // 여기서 한 번만 String 객체 생성
        endTime = System.currentTimeMillis();
        System.out.println("StringBuilder 시간: " + (endTime - startTime) + "ms");
        System.out.println("최종 문자열 길이: " + result2.length());

        System.out.println("\n=== StringBuilder 주요 메서드 상세 ===");

        // StringBuilder 생성과 기본 동작
        StringBuilder builder = new StringBuilder();
        System.out.println("빈 StringBuilder 생성 - 용량: " + builder.capacity());

        // append() - 문자열을 뒤에 추가
        builder.append("Hello");    // "Hello"
        System.out.println("append('Hello'): " + builder.toString());
        builder.append(" ");        // "Hello "
        builder.append("World");    // "Hello World"
        System.out.println("최종 append 결과: " + builder.toString());
        System.out.println("현재 길이: " + builder.length() + ", 용량: " + builder.capacity());

        // insert() - 지정된 위치에 문자열 삽입
        builder.insert(5, ",");  // 5번 인덱스(공백 위치)에 쉼표 삽입
        System.out.println("insert(5, ','): " + builder.toString()); // "Hello, World"

        // delete() - 지정된 범위의 문자 삭제
        builder.delete(5, 6);    // 5번 인덱스부터 6번 인덱스 전까지 삭제 (쉼표 삭제)
        System.out.println("delete(5, 6): " + builder.toString()); // "Hello World"

        // replace() - 지정된 범위를 다른 문자열로 교체
        builder.replace(6, 11, "Java");  // "World"를 "Java"로 교체
        System.out.println("replace(6, 11, 'Java'): " + builder.toString()); // "Hello Java"

        // reverse() - 전체 문자열 뒤집기
        builder.reverse();
        System.out.println("reverse(): " + builder.toString()); // "avaJ olleH"

        System.out.println("\n=== StringBuffer vs StringBuilder 차이점 ===");

        /*
         * StringBuffer와 StringBuilder의 차이점:
         *
         * StringBuffer (JDK 1.0부터):
         * - 모든 메서드가 synchronized로 동기화됨
         * - 멀티스레드 환경에서 안전함 (thread-safe)
         * - 동기화 오버헤드로 인해 상대적으로 느림
         * - 멀티스레드에서 문자열 조작이 필요할 때 사용
         *
         * StringBuilder (JDK 1.5부터):
         * - 동기화되지 않음 (non-synchronized)
         * - 싱글스레드 환경에서 사용
         * - 동기화 오버헤드가 없어 빠름
         * - 대부분의 경우 StringBuilder 사용 권장
         */

        // StringBuffer - 동기화됨 (thread-safe하지만 느림)
        StringBuffer buffer = new StringBuffer("StringBuffer: ");
        buffer.append("멀티스레드 환경에서 안전하지만 ");
        buffer.append("동기화 오버헤드로 인해 느림");
        System.out.println(buffer.toString());

        // StringBuilder - 동기화되지 않음 (빠르지만 thread-unsafe)
        StringBuilder builder2 = new StringBuilder("StringBuilder: ");
        builder2.append("싱글스레드 환경에서 빠르지만 ");
        builder2.append("멀티스레드에서는 안전하지 않음");
        System.out.println(builder2.toString());

        System.out.println("\n=== 내부 용량(capacity) 관리 ===");

        /*
         * StringBuilder 용량 관리:
         * - 초기 용량: 16 (기본) 또는 지정된 크기
         * - 용량 부족 시 자동 확장: (현재용량 * 2) + 2
         * - 메모리 효율성을 위해 예상 크기로 초기화 권장
         */

        StringBuilder capacityTest = new StringBuilder(10); // 초기 용량 10으로 설정
        System.out.println("초기 상태 - 길이: " + capacityTest.length() + ", 용량: " + capacityTest.capacity());

        capacityTest.append("12345");
        System.out.println("'12345' 추가 후 - 길이: " + capacityTest.length() + ", 용량: " + capacityTest.capacity());

        capacityTest.append("67890");
        System.out.println("'67890' 추가 후 - 길이: " + capacityTest.length() + ", 용량: " + capacityTest.capacity());

        // 용량을 초과하는 문자열 추가 시 자동 확장
        capacityTest.append("ABCDEFGHIJK");  // 용량 초과
        System.out.println("긴 문자열 추가 후 - 길이: " + capacityTest.length() + ", 용량: " + capacityTest.capacity());

        // ensureCapacity()로 용량 미리 확보
        StringBuilder preAllocated = new StringBuilder();
        preAllocated.ensureCapacity(1000);  // 1000자리 미리 확보
        System.out.println("ensureCapacity(1000) 후 용량: " + preAllocated.capacity());

        System.out.println("\n=== 실제 사용 예제 ===");

        // HTML 태그 생성 예제
        StringBuilder htmlBuilder = new StringBuilder();
        String[] items = {"사과", "바나나", "오렌지", "포도"};

        htmlBuilder.append("<ul>\n");
        for (String item : items) {
            htmlBuilder.append("  <li>").append(item).append("</li>\n");
        }
        htmlBuilder.append("</ul>");

        System.out.println("생성된 HTML:");
        System.out.println(htmlBuilder.toString());

        // 파일 경로 생성 예제
        StringBuilder pathBuilder = new StringBuilder();
        String[] pathParts = {"home", "user", "documents", "projects", "java"};

        for (int i = 0; i < pathParts.length; i++) {
            pathBuilder.append(pathParts[i]);
            if (i < pathParts.length - 1) {
                pathBuilder.append("/");
            }
        }

        System.out.println("생성된 경로: " + pathBuilder.toString());

    }
}
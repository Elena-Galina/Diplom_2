package stellarburgers.user;

import net.datafaker.Faker;

import java.util.Locale;

public class UserGenerator {

    public static User randomeUser() {
        return new User()
                .setName(randomName())
                .setEmail(randomName() + "@yandex.com")
                .setPassword(randomPassword());
    }

    public static String randomName() {
        Faker faker = new Faker(new Locale("en-US"));
        return faker.name().firstName();
    }

    public static String randomPassword() {
        Faker faker = new Faker();
        return String.valueOf(faker.number().numberBetween(100000, 1000000));
    }

    public static String randomNumber() {
        Faker faker = new Faker();
        return String.valueOf(faker.number().randomNumber());
    }
}

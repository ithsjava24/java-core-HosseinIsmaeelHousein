package org.example;

import java.nio.file.Path;

public class ReadFromFile {
    public static void main(String[] args) {

        // Correctly using Path.of() method
        Path path = Path.of("c:\\dev\\licenses\\windows\\readme.txt");
        System.out.println(path);

        path = Path.of("c:/dev/licenses/windows/readme.txt");
        System.out.println(path);

        System.out.println(System.getProperty("user.home"));

        Path myPath = Path.of(System.getProperty("user.home"), "readme.txt");
        System.out.println(myPath);

        Path currentPath = Path.of(System.getProperty("user.dir"));
        System.out.println(currentPath);

    }
}

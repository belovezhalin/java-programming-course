package uj.wmii.pwj.w7.insurance;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FloridaInsurance {

    public static void main(String[] args) {
        List<InsuranceEntry> entries = readInsuranceData();
        generateCountryCountFile(entries);
        generateTiv2012SumFile(entries);
        generateMostValuableFile(entries);
    }

    private static List<InsuranceEntry> readInsuranceData() {
        List<InsuranceEntry> entries = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile("FL_insurance.csv.zip")) {
            ZipEntry entry = zipFile.entries().nextElement();
            if (!entry.isDirectory()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)))) {
                    reader.lines()
                            .skip(1)
                            .forEach(line -> entries.add(new InsuranceEntry(line)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private static void generateCountryCountFile(List<InsuranceEntry> entries) {
        long uniqueCountries = entries.stream()
                .map(InsuranceEntry::getCountry)
                .distinct()
                .count();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("count.txt"))) {
            writer.write(String.valueOf(uniqueCountries));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateTiv2012SumFile(List<InsuranceEntry> entries) {
        BigDecimal totalTiv2012 = entries.stream()
                .map(InsuranceEntry::getTiv2012)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tiv2012.txt"))) {
            writer.write(totalTiv2012.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateMostValuableFile(List<InsuranceEntry> entries) {
        List<Map.Entry<String, BigDecimal>> sortedCountries = entries.stream()
                .collect(Collectors.groupingBy(
                        InsuranceEntry::getCountry,
                        Collectors.mapping(
                                entry -> entry.getTiv2012().subtract(entry.getTiv2011()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .toList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("most_valuable.txt"))) {
            writer.write("country,value\n");
            for (Map.Entry<String, BigDecimal> entry : sortedCountries) {
                writer.write(entry.getKey() + "," + entry.getValue().setScale(2, RoundingMode.HALF_UP) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

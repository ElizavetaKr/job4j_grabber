package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final int PAGES = 5;
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public static void main(String[] args) throws IOException {
        for (int pageNumber = 1; pageNumber <= PAGES; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element dataElement = row.child(0).child(0);
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String vacancyData = dataElement.attr("datetime");
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s %n", vacancyName, link, vacancyData);
            });

        }
    }

    private String retrieveDescription(String link) throws IOException {
        StringBuilder description = new StringBuilder();
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-description__text");
        rows.forEach(
                row -> {
                    Element aboutCompanyHeading = row.child(0);
                    description.append(aboutCompanyHeading.text() + "\n");
                    Element aboutCompany = row.child(1);
                    description.append(aboutCompany.text() + "\n");

                    Element expectationsHeading = row.child(2);
                    description.append(expectationsHeading.text() + "\n");
                    Element expectations = row.child(3);
                    description.append(expectations.text() + "\n");

                    Element conditionsHeading = row.child(4);
                    description.append(conditionsHeading.text() + "\n");
                    Element conditions = row.child(5);
                    description.append(conditions.text() + "\n");

                });

        return description.toString();
    }
}
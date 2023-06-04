import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HtmlAnalyzer {
    private ArrayList<Double> altsInAnchors = new ArrayList<>();
    private ArrayList<Double> altsInImages = new ArrayList<>();
    private int totalImages = 0;
    private int totalImagesWithAlts = 0;
    private int totalImagesWithEmptyAlts = 0;
    private int totalAnchors = 0;
    private int totalAnchorsWithAlts = 0;
    private int totalAnchorsWithEmptyAlts = 0;
    private int htmlPagesNumber = 0;
    private int pageLanguageCount = 0;

    public void overallUsagePercentage() {
        double totalAnchorRatio = 0.0;
        double totalImageRatio = 0.0;
        for (Double ratio : altsInAnchors) {
            totalAnchorRatio += ratio;
        }
        for (Double ratio : altsInImages) {
            totalImageRatio += ratio;
        }

        double overallAnchorPercentage = (totalAnchorRatio / htmlPagesNumber);
        double overallImagePercentage = (totalImageRatio / htmlPagesNumber);

        System.out.println("----------Summary----------");
        System.out.println("Number of websites: " + htmlPagesNumber);
        System.out.println("Number of websites specifying page language: " + pageLanguageCount);
        System.out.println("Number of images counted in these websites: " + totalImages);
        System.out.println("Number of images with alt attribute counted in these websites: " + totalImagesWithAlts);
        System.out.println("Number of images with empty alt attribute counted in these websites: " + totalImagesWithEmptyAlts);
        System.out.println("Number of images without alt attribute counted in these websites: " + (totalImages - (totalImagesWithAlts + totalImagesWithEmptyAlts)));
        System.out.println("Number of anchors counted in these websites: " + totalAnchors);
        System.out.println("Number of anchors with alt attribute counted in these websites: " + totalAnchorsWithAlts);
        System.out.println("Number of anchors with empty alt attribute counted in these websites: " + totalAnchorsWithEmptyAlts);
        System.out.println("Number of anchors without alt attribute counted in these websites: " + (totalAnchors - (totalAnchorsWithAlts + totalAnchorsWithEmptyAlts)));
        System.out.println();
        System.out.println("----------Percentages----------");
        System.out.println("Average usage for <a> tags with alt attribute per page: " + String.format("%.2f", overallAnchorPercentage) + "%");
        System.out.println("Average usage for <img> tags with alt attribute per page: " + String.format("%.2f", overallImagePercentage) + "%");
        System.out.println("Overall page language specified: " + pageLanguageCount + "%");
    }

    public void extractHtmlText(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    extractHtmlText(file.getAbsolutePath());
                } else if (file.getName().endsWith(".html")) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }
                        String html = sb.toString();
                        System.out.println("-" + directory.getName() + ":");
                        anchorAltCounter(html);
                        imageAltCounter(html);
                        checkPageLanguage(html);
                        System.out.println("");
                        htmlPagesNumber++;
                    } catch (IOException e) {
                        System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void anchorAltCounter(String html) {
        int count = 0;
        int altCount = 0;
        int emptyAlts = 0;

        int index = 0;
        while (index < html.length()) {
            int start = html.indexOf("<a ", index);
            if (start == -1) {
                break;
            }
            int end = html.indexOf(">", start);
            if (end == -1) {
                break;
            }
            String anchor = html.substring(start, end + 4);
            if (anchor.contains("alt=")) {
                if (anchor.contains("alt=\"\"") || anchor.contains("alt=''") ||
                        anchor.contains("alt=\" \"") || anchor.contains("alt=' '")) {
                    emptyAlts++;
                } else {
                    altCount++;
                }
            }

            count++;
            index = end + 4;
        }
        System.out.println("The file contains " + count + " <a> tags, with " + altCount +
                " having alt attribute and " +
                emptyAlts + " having empty alt attribute.");
        totalAnchors += count;
        totalAnchorsWithAlts+=altCount;
        totalAnchorsWithEmptyAlts+=emptyAlts;
        altsInAnchors.add(((double) altCount / (double) count) * 100);
    }

    private void imageAltCounter(String html) {
        int count = 0;
        int altCount = 0;
        int emptyAlts = 0;

        int index = 0;
        while (index < html.length()) {
            int start = html.indexOf("<img ", index);
            if (start == -1) {
                break;
            }
            int end = html.indexOf(">", start);
            if (end == -1) {
                break;
            }
            String imgTag = html.substring(start, end + 1);
            if (imgTag.contains("alt=")) {
                if (imgTag.contains("alt=\"\"") || imgTag.contains("alt=''") ||
                        imgTag.contains("alt=\" \"") || imgTag.contains("alt=' '")) {
                    emptyAlts++;
                } else {
                    altCount++;
                }
            }
            count++;
            index = end + 1;
        }

        System.out.println("The file contains " + count + " <img> tags, with " + altCount +
                " having alt attribute and " +
                emptyAlts + " having empty alt attribute.");
        totalImages += count;
        totalImagesWithAlts+=altCount;
        totalImagesWithEmptyAlts+=emptyAlts;
        altsInImages.add(((double) altCount / (double) count) * 100);
    }

    private void checkPageLanguage(String html) {
        if (html.contains("lang=") || html.contains("xml:lang=") || html.contains("lang =") || html.contains("xml:lang =")) {
            pageLanguageCount++;
        }else{
            System.out.println("No language specified for this page.");
        }
    }
}
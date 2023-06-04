public class Main {
    public static void main(String[] args) {
        HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
        htmlAnalyzer.extractHtmlText("PATH HERE");
        htmlAnalyzer.overallUsagePercentage();
    }
}
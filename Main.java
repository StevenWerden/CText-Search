import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Scanner;

class Main {
  public static void main(String[] args) throws IOException {
    System.out.println("\n");
    // testProgram();
    runProgram();
  }

  private static void runProgram() throws IOException {
    String[] differentTexts = {
        "https://ctext.org/zhuangzi",
        "https://ctext.org/analects",
        "https://ctext.org/mengzi",
        "https://ctext.org/xunzi",
        "https://ctext.org/dao-de-jing",
        "https://ctext.org/chu-ci" };
    String[] namesTexts = {
        "莊子 - Zhuangzi",
        "論語 - The Analects",
        "孟子 - Mengzi",
        "荀子 - Xunzi",
        "道德經 - Dao De Jing",
        "楚辭 - Chu Ci",
        "Custom link" };

    int index = 1;
    while (index < namesTexts.length + 1) {
      System.out.println(index + " - " + namesTexts[index - 1]);
      index += 1;
    }

    System.out.println("\nWhere would you like to start?");
    boolean asker = true;
    String textToUse = "";
    int chooseText = -1;
    Scanner reader = new Scanner(System.in, "UTF-8");
    while (asker == true) {
      String currentText = reader.nextLine();
      chooseText = Integer.valueOf(currentText);
      if ((chooseText <= 0) || (chooseText >= 8)) {
        System.out.println("Please pick a valid option.");
      } else if (chooseText == 7) {
        asker = false;
        runCustomLink();
      } else {
        asker = false;
      }
    }
    String currentChar = "";
    if (chooseText != 7) {
      System.out.println("\nPlease paste what you would like to search for:");
      asker = true;
      while (asker == true) {
        currentChar = reader.nextLine();
        if (currentChar.length() < 1) {
          System.out.println("Please paste a valid character.");
        } else {
          textToUse = differentTexts[chooseText - 1];
          asker = false;
        }
      }
      System.out.println("\n Progress: Gathering data.");
      List<String> allLinks = linksSearch(textToUse);
      System.out.println("\n Progress: All data has been gathered.");
      findCharsInLinks(allLinks, currentChar);
      findMatches(textToUse + "?searchu=" + currentChar, currentChar);
    }
    reader.close();
  }

  /*
   * This runs the basic program of giving a link and character.
   */
  private static void runCustomLink() throws IOException {
    System.out.println("\nPlease paste the URL here:\n");
    Scanner reader = new Scanner(System.in, "UTF-8");
    String urlLink = reader.nextLine();

    System.out.println();

    System.out.println("Please paste what you would like to search for:\n");
    String currentChar = reader.nextLine();

    reader.close();
    // https://ctext.org/zhuangzi
    // https://ctext.org/liezi
    // 古
    // 真
    // 之以 - 81 in zhuangzi
    System.out.println("\n Progress: Gathering data.");
    List<String> allLinks = linksSearch(urlLink);
    System.out.println("\n Progress: All data has been gathered.");
    findCharsInLinks(allLinks, currentChar);
    findMatches(urlLink + "?searchu=" + currentChar, currentChar);
  }

  /*
   * This automates the inputs to make running tests easier.
   */
  private static void testProgram() throws IOException {
    String urlLink = "https://ctext.org/analects";
    System.out.println("Current link is: \n" + urlLink + "\n");
    String currentChar = "北";
    System.out.println("Current char is: \n" + currentChar + "\n");

    System.out.println("\n Progress: Gathering data.");
    List<String> allLinks = linksSearch(urlLink);
    System.out.println("\n Progress: All data has been gathered.");
    findCharsInLinks(allLinks, currentChar);
    findMatches(urlLink + "?searchu=" + currentChar, currentChar);
  }

  /*
   * This searches through all the links of a specific author and gives all the
   * sublinks of their works
   */
  private static List<String> linksSearch(String urlLink) throws IOException {
    List<String> firstLayer = new ArrayList<String>();
    firstLayer.add(urlLink);
    // Find all the links on the starter page to establish the first layer.
    List<String> currentLink = fetchHTML(urlLink);
    String[] splitURL = urlLink.split("/");
    String findStory = splitURL[splitURL.length - 1] + "/";
    String subStory = "";
    try {
      subStory = splitURL[splitURL.length - 2] + "/";
    } catch (ArrayIndexOutOfBoundsException e) {
      // Length 1
    }
    int cap = 0;
    for (String item : currentLink) {
      String newURL = checkItem(splitURL, urlLink, item, findStory, subStory);
      if (newURL.equals("《》") && cap == 1) {
        firstLayer.remove(firstLayer.size() - 1);
        cap = 0;
      } else if (newURL.length() > 0 && !firstLayer.contains(newURL) && !newURL.contains("#")) {
        firstLayer.add(newURL);
        cap = 1;
      }
    }
    // Find all the links from each link the starter page gives
    // This is done because there will at most be two layers of links to search
    // through for some books.
    List<String> allLinks = new ArrayList<String>();
    allLinks.add(urlLink);
    for (String item : firstLayer) {
      currentLink = fetchHTML(item);
      splitURL = urlLink.split("/");
      findStory = splitURL[splitURL.length - 1] + "/";
      try {
        subStory = splitURL[splitURL.length - 2] + "/";
      } catch (ArrayIndexOutOfBoundsException e) {
        // Length 1
      }
      int flag = 0;
      for (String current : currentLink) {
        String newURL = checkItem(splitURL, item, current, findStory, subStory);
        if (newURL.equals("《》") && flag == 1) {
          allLinks.remove(allLinks.size() - 1);
          flag = 0;
        } else if (newURL.length() > 0 && !allLinks.contains(newURL) && !newURL.contains("#")
            && !newURL.equals("《》")) {
          allLinks.add(newURL);
          flag = 1;
        }
      }
    }
    return allLinks;
  }

  /*
   * This checks for all the variations that an item could have that we want. An
   * item can be defined
   * as a piece of text from a page that may contain the variables needed for a
   * link.
   */
  private static String checkItem(String[] splitURL, String urlLink, String item, String findStory, String subStory) {
    if (item.contains("popup") && item.contains("</a>")) {
      return "《》";
    }
    if (item.contains("href=\"" + findStory)) {
      String[] splitItem = item.split("\"");
      splitURL[splitURL.length - 1] = splitItem[splitItem.length - 1];
      String newURL = String.join("/", splitURL);
      String[] slashCount = newURL.split("/");
      if (blacklistSearch(urlLink, newURL) && slashCount.length == 4 || slashCount.length == 5) {
        return newURL;
      }
    } else if (item.contains("href=\"" + subStory) && subStory != "") {
      String[] splitItem = item.split("\"");
      splitURL[splitURL.length - 2] = splitItem[splitItem.length - 1];
      splitURL[splitURL.length - 1] = "";
      String newURL = String.join("/", splitURL);
      String[] slashCount = newURL.split("/");
      if (blacklistSearch(urlLink, newURL) && slashCount.length == 4 || slashCount.length == 5) {
        return newURL;
      }
    } else if (item.contains("href") && !item.contains("#") && !item.contains(".")
        && !item.contains("?") && !item.contains("<") && !item.contains(">")
        && !item.contains(":") && !item.contains("#")) {
      String[] splitItem = item.split("\"");
      String newURL = "https://ctext.org/" + splitItem[splitItem.length - 1];
      String[] slashCount = newURL.split("/");
      if (blacklistSearch(urlLink, newURL) && slashCount.length == 4 || slashCount.length == 5) {
        return newURL;
      }
    }
    return "";
  }

  // Blacklist the links that present the page in traditional/simplified Chinese
  private static boolean blacklistSearch(String urlLink, String newURL) {
    if (((urlLink + "/ens").equals(newURL)) || ((urlLink + "/zh").equals(newURL))) {
      return false;
    }
    return true;
  }

  /*
   * This function grabs all the HTML data from the given link and returns its
   * contents
   */
  private static List<String> fetchHTML(String urlLink) throws IOException {
    List<String> contents = new ArrayList<String>();
    try {
      URL url = new URL(urlLink);
      Scanner is = new Scanner(url.openStream());
      int hitsContent = 0;
      while (is.hasNext()) {
        if (hitsContent != 0) {
          contents.add(is.next());
        } else {
          contents.add(is.next());
          String item = contents.get(contents.size() - 1);
          if (item.contains("content2") || item.contains("content3")) {
            hitsContent = 1;
          } else {
            contents.remove(contents.size() - 1);
          }
        }
      }
    } catch (MalformedURLException e) {
      // Wrong URL
      System.out.println("URL DID NOT WORK");
    } catch (IOException e) {
      // Usually this is HTTP 403 (Forbidden)
      System.out.println("URL DID NOT WORK");
      //System.exit(0);
    }
    return contents;
  }

  /*
   * ctext counts characters next to each other despite having commas or things in
   * between
   * so this removes some of it to match the website.
   */
  private static String filterText(String item) {
    item = item.replace("，", "");
    item = item.replace("。", "");
    item = item.replace("：", "");
    item = item.replace("「", "");
    item = item.replace("」", "");
    return item;
  }

  /*
   * This removes all the alphabetical and html characters that are used to
   * identify
   * parts of the page that have Chinese to isolate only the Chinese
   */
  private static String postFilter(String item) {
    item = item.replaceAll("(?U)\\P{Alnum}+", ""); // </> and punctuation
    item = item.replaceAll("\\d", ""); // numbers
    item = item.replace("idcommdiv", ""); // html metadata
    if (item.contains("abspan") || // grayed out Chinese before content
        item.contains("relnofollow") || // website features labeled in Chinese{
        item.contains("classpopup")) {// links to other literature we dont want
      return "";
    }
    item = item.replace("tdtr", ""); // random text at the end of each piece of content
    item = item.replace("classrefindexsup", ""); // used in reference numbers (exponents)
    for (int i = 0; i < item.length();) {
      int codepoint = item.codePointAt(i);
      i += Character.charCount(codepoint);
      if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
        return item;
      }
    }
    return "";
  }

  private static void findMatches(String link, String currentChar) throws IOException {
    int count = 0;
    try {
      URL url = new URL(link);
      Scanner is = new Scanner(url.openStream());
      while (is.hasNext() && count < 1) {
        String item = is.next();
        if (item.contains("Matched")) {
          item = item.replaceAll("[^0-9]", "");
          count = Integer.valueOf(item);
        }
      }
    } catch (MalformedURLException e) {
      System.out.println("MATCHES URL DID NOT WORK");
    }
    System.out.println("\n Total number of " + currentChar + ": " + count);
  }

  private static void printDataset(HashMap<Character, Integer> dataset) {
    System.out.println(dataset);
  }

  /*
   * Searches for different types of characters and frequencies given the below
   * critera:
   * totalCharacters = The total amount of Chinese characters.
   * currentCharacters = The total amount of a certain Chinese character.
   * distinctCharacters = The amount of different types of Chinese characters.
   * uniqueCharacters = The characters that only appear once.
   * rareCharacters = The characters that appear 2-5 times.
   * hapaxLegomenoi = Characters that only appear once across all literature.
   */
  private static void findCharsInLinks(List<String> allLinks, String currentChar) throws IOException {
    int totalCharacters = 0;
    HashMap<Character, Integer> uniqueRareList = new HashMap<Character, Integer>();
    int progressIndex = 0;
    int linkProgress = 0;
    int progress[] = new int[] { 25, 50, 75, 95 };
    for (String link : allLinks) {
      linkProgress += 1;
      if ((progressIndex < 4 && (((linkProgress * 100) / allLinks.size()) > progress[progressIndex]))) {
        System.out.println("\n Progress: Search is " + progress[progressIndex] + "% complete.");
        progressIndex += 1;
      }
      System.out.println(link);
      List<String> contents = fetchHTML(link);
      for (String item : contents) {
        item = filterText(item);
        if ((item.contains("</div>") || item.contains("</a>") || item.contains("</sup>"))
            && !item.contains("href")) {
          item = postFilter(item);
          if (item.length() > 0) {
            //System.out.println(item); //if you want to look at all the Chinese
            int i = 0;
            while (i < item.length()) {
              char currentItem = item.charAt(i);
              totalCharacters += 1;
              i += 1;
              Integer count = uniqueRareList.get(currentItem);
              if (count == null) {
                count = 0;
              }
              uniqueRareList.put(currentItem, count + 1);
            }
          }
        }
      }
    }
    System.out.println("\n Total number of characters = " + totalCharacters);
    System.out.println("\n Number of distinct characters = " + uniqueRareList.size());
    HashMap<Character, Integer> uniqueList = new HashMap<Character, Integer>();
    HashMap<Character, Integer> rareList = new HashMap<Character, Integer>();
    uniqueRareList.forEach((k, v) -> {
      if (v == 1) {
        uniqueList.put(k, v);
      }
      if (v >= 2 && v <= 5) {
        rareList.put(k, v);
      }
    });
    float uniqueFeq = (100 * uniqueList.size()) / totalCharacters;
    System.out.println("\n Number of unique characters = " + uniqueList.size());
    System.out.println("Frequency of unique characters = " + String.format("%.0f%%", uniqueFeq));
    printDataset(uniqueList);
    float rareFeq = (100 * rareList.size()) / totalCharacters;
    System.out.println("\n Number of rare characters = " + rareList.size());
    System.out.println("Frequency of rare characters = " + String.format("%.0f%%", rareFeq));
    printDataset(rareList);
  }
}
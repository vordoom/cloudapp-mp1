import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now", ""};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    private String getRegexDelimiters(){

        StringBuilder regexp = new StringBuilder("");
        regexp.append("[");

        for (int i = 0; i < this.delimiters.length(); i++) {
            regexp.append(Pattern.quote(
                    Character.toString(this.delimiters.charAt(i))
            ));
        }
        regexp.append("]");

        return regexp.toString();
    }

    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<>(map.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue()) * -1;
            }
        });

        Map<K,V> sortedMap = new LinkedHashMap<>();

        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map){
        List<K> keys = new LinkedList<>(map.keySet());
        Collections.sort(keys);

        Map<K,V> sortedMap = new LinkedHashMap<>();
        for(K key: keys){
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
        Hashtable<String, Integer> wordsRank = new Hashtable<>();
        List<String> fileLines = getFileLines();
        Integer[] indexes = getIndexes();
        String delimitersRegex = getRegexDelimiters();
        HashSet<String> stopWordsSet = new HashSet<>(Arrays.asList(this.stopWordsArray));

        for(Integer i : indexes){
            String currentLine = fileLines.get(i).toLowerCase().trim();

            // tokenize
            String[] result = currentLine.split(delimitersRegex);

            for(String s : result){
                // check stop list
                if(stopWordsSet.contains(s))
                    continue;

                Integer v = 0;

                // update hash table
                if(wordsRank.containsKey(s)) {
                    v = wordsRank.get(s);
                }

                v++;
                wordsRank.put(s, v);
            }
        }

        // sort key and values
        Map<String, Integer> sortedResult = sortByKeys(wordsRank);
        sortedResult = sortByValues(sortedResult);

        // fill in resultant array
        int i=0;
        for(String k : sortedResult.keySet()){
            ret[i]=k;
            i++;

            if(i == ret.length)
                break;
        }

        return ret;
    }

    private List<String> getFileLines() throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.inputFileName))) {

            String line;
            while ((line = br.readLine()) != null) lines.add(line);

            br.close();
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return lines;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "c:\\input.txt";

            // TODO: uncomment
//            String inputFileName = "./input.txt";

            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}

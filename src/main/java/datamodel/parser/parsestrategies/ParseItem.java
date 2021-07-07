package datamodel.parser.parsestrategies;

import datamodel.objects.Article;
import datamodel.objects.Item;
import datamodel.parser.Parser;
import local.Markers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseItem implements ParseStrategy {

    /**
     * Returns a list containing a single item obtained by parsing the input hex string.
     * <p>
     * Hex string has to be from a file in prefab/item.
     *
     * @param splitString a hex string with spaces inserted every 2 characters, which is from a file in prefab/item
     * @return an Item object in a list, where the item is formed from the input string
     */
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {

        // instantiate the stuff needed to parse
        Markers m = new Markers();
        String recPathStartMarker = "item\\";
        String recPathEndMarker = ".binfab";

        // instantiate the variables
        String name, desc, rPath;
        String[] unlocks = new String[0];

        // subset relative path
        int rPathStart = absPath.indexOf(recPathStartMarker);
        int rPathEnd = absPath.indexOf(recPathEndMarker);

        if (rPathStart != -1 && rPathEnd != -1) {
            rPath = absPath.substring(rPathStart, rPathEnd);
            rPath = rPath.replaceAll("\\\\", "/");
        } else {
            throw new ParseException(absPath + "could not be used to form a proper relative path.");
        }

        // identify name and desc paths
        Pattern ndp = Pattern.compile("[0-9]8 ([0-9A-F][0-9A-F]) (24 ([0-9A-F][0-9A-F] )+)([0-9A-F][0-9A-F] )+?(00 |([0-9A-F][0-9A-F]) (24 ([0-9A-F][0-9A-F] )+))68 00 80");
        Pattern bp = Pattern.compile("[0-9]8 ([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )+)2E 62 6C 75 65 70 72 69 6E 74");
        Pattern bp2 = Pattern.compile("[0-9]8 ([0-9A-F][0-9A-F]) (((3[0-9]|4[0-9A-F]|5[0-9AF]|6[1-9A-F]|7[0-9A]) )+)");

        int ndEnd = splitString.indexOf("68 00 80");
        if (ndEnd == -1) {
            throw new ParseException(rPath + " did not have an end marker.");
        }

        Matcher ndm = ndp.matcher(splitString.substring(0, ndEnd + 8));
        if (!ndm.find()) {
            throw new ParseException(rPath + " did not match the pattern for name and description; does it satisfy the assumptions?");
        }
        int nLen = Integer.parseInt(ndm.group(1), 16);
        name = Parser.hexToAscii(ndm.group(2).length() <= 3 * nLen ? ndm.group(2) : ndm.group(2).substring(0, 3 * nLen));

        if (ndm.group(5).equals("00 ")) {
            desc = null;
        } else {
            int dLen = Integer.parseInt(ndm.group(6), 16);
            desc = Parser.hexToAscii(ndm.group(7).length() <= 3 * dLen ? ndm.group(7) : ndm.group(7).substring(0, 3 * dLen));
        }

        String remaining = splitString.substring(ndm.end());
        List<String> bTxt = new ArrayList<>();
        int bLen;

        Matcher bm = bp.matcher(remaining);
        if (bm.find()) {
            bLen = Integer.parseInt(bm.group(1), 16);
            bTxt.add(Parser.hexToAscii(bm.group(2).length() <= 3 * bLen ? bm.group(2) : bm.group(2).substring(0, 3 * bLen)));
        } else {
            Matcher bm2 = bp2.matcher(remaining);
            List<String> options = new ArrayList<>();
            while (bm2.find()) {
                bLen = Integer.parseInt(bm2.group(1), 16);
                options.add(Parser.hexToAscii(bm2.group(2).length() <= 3 * bLen ? bm2.group(2) : bm2.group(2).substring(0, 3 * bLen)));
            }

            if (options.size() == 0) throw new ParseException(rPath + ": no possible blueprint found.");
            else bTxt.addAll(options);
        }

        // identify if collection exists
        if (splitString.contains(m.collection)) {
            List<String> collection = new ArrayList<>();

            // locate all the markers
            int colIndex = splitString.indexOf(m.collection);
            String currString = splitString.substring(colIndex);

            while (currString.contains(m.collection)) {
                int index = currString.indexOf(" 28 00");
                collection.add(Parser.hexToAscii(currString.substring(0, index)));

                // go to next collection
                if (currString.substring(index).contains(m.collection)) {
                    currString = currString.substring(currString.indexOf(m.collection, index));
                } else {
                    break;
                }
            }

            unlocks = collection.toArray(new String[0]);
        }

        // identify if lootbox exists
        boolean lootbox = splitString.contains(m.lootbox);

        return new Item(name, desc, rPath, unlocks, bTxt.toArray(new String[0]), lootbox);
    }

    // creating a new item:
    // read binary file, insert spaces
    // identify name and description paths, if they exist -> just stop here if does not exist
    // description breaker: 68 00 80

    // identify if they unlock things - optional, note that they can unlock more than 1
    // string to identify: 63 6F 6C 6C 65 63 74 69 6F 6E 73 2F - collections/

    // identify if they are lootboxes
    // string to identify: 4C 6F 6F 74 54 61 62 6C 65 - LootTable

    // identify blueprint, may be useful?
    // string to identify: 1E 4A - need to add offset (+15 is the start of the blueprint)
    // string blueprint ends in: 2E 62 6C 75 65 70 72 69 6E 74 - .blueprint
    // not all files contain blueprints, strangely enough
}

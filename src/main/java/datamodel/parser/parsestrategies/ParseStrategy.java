package datamodel.parser.parsestrategies;

import datamodel.objects.Article;

interface ParseStrategy {

    Article parseObject(String splitString, String absPath) throws ParseException;
}

JAVAC=javac
JAVAC_FLAGS=-sourcepath .
JAVA=java
JAVA_FLAGS=-classpath ${CLASSPATH}
CLASSPATH=/home/falleh/.local/lib/antlr-4.8-complete.jar
CLASSPATH2=/home/falleh/.local/lib/antlr-4.8-complete.jar
PARSER=${JAVA} ${JAVA_FLAGS} org.antlr.v4.Tool
PARSER_FLAGS=-lib lily -package lily
LEXER=${JAVA} ${JAVA_FLAGS} org.antlr.v4.Tool
LEXER_FLAGS=-lib lily -package lily
all : test

test : absyn lily/lilyLexer.class lily/PrettyPrinter.class lily/Test.class lily/VisitSkel.class lily/ComposVisitor.class lily/AbstractVisitor.class lily/FoldVisitor.class lily/AllVisitor.class lily/lilyParser.class lily/Test.class

.PHONY : absyn

%.class : %.java
	${JAVAC} ${JAVAC_FLAGS} $^

absyn : lily/Absyn/Score.java lily/Absyn/ScoreBlock.java lily/Absyn/MusicExpr.java lily/Absyn/CompoundExp.java lily/Absyn/ExpList.java lily/Absyn/NoteExp.java lily/Absyn/ListMusicExpr.java
	${JAVAC} ${JAVAC_FLAGS} $^

lily/lilyLexer.java : lily/lilyLexer.g4
	${LEXER} ${LEXER_FLAGS} lily/lilyLexer.g4

lily/lilyParser.java : lily/lilyParser.g4
	${PARSER} ${PARSER_FLAGS} lily/lilyParser.g4

lily/lilyLexer.class : lily/lilyLexer.java lily/lilyParser.java

lily/lilyParser.class : lily/lilyParser.java

lily/PrettyPrinter.class : lily/PrettyPrinter.java

clean :
	rm -f lily/Absyn/*.class lily/*.class

distclean : vclean

vclean :
	 rm -f lily/Absyn/Score.java lily/Absyn/ScoreBlock.java lily/Absyn/MusicExpr.java lily/Absyn/CompoundExp.java lily/Absyn/ExpList.java lily/Absyn/NoteExp.java lily/Absyn/ListMusicExpr.java lily/Absyn/Score.class lily/Absyn/ScoreBlock.class lily/Absyn/MusicExpr.class lily/Absyn/CompoundExp.class lily/Absyn/ExpList.class lily/Absyn/NoteExp.class lily/Absyn/ListMusicExpr.class
	 rm -f lily/Absyn/*.class
	 rmdir lily/Absyn/
	 rm -f lily/lilyLexer.g4 lily/lilyParser.g4 lily/lilyLexer.java lily/VisitSkel.java lily/ComposVisitor.java lily/AbstractVisitor.java lily/FoldVisitor.java lily/AllVisitor.java lily/PrettyPrinter.java lily/Skeleton.java lily/Test.java lily/lilyParser.java lily/*.class lily/lilyLexer.tokens lily/lilyLexerBaseListener.java lily/lilyLexerListener.java lily/lilyParser.tokens lily/lilyParserBaseListener.java lily/lilyParserListener.java
	 rm -f Makefile
	 rmdir -p lily/

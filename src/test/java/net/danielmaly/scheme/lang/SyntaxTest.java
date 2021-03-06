package net.danielmaly.scheme.lang;

import net.danielmaly.scheme.FileBasedTest;
import org.junit.Test;


public class SyntaxTest extends FileBasedTest {

    @Test
    public void testBottlesOfBeer() throws Exception {
        this.assertOutputMatches("/bottles.scm", "/bottles.scm.result");
    }

    @Test
    public void testRec() throws Exception {
        this.assertOutputMatches("/rec.scm", "/rec.scm.result");
    }

    @Test
    public void testSyntax() throws Exception {
        this.assertOutputMatches("/syntax.scm", "/syntax.scm.result");
    }

    @Test
    public void testInput() throws Exception {
        this.assertOutputMatches("/input.scm", "/input.scm.result", "/input.scm.stdin");
    }
}

package org.metatype.sxc.builder.impl;

import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JStatement;

public class JLineComment implements JStatement {
    private final String comment;

    public JLineComment(String comment) {
        if (comment == null) throw new NullPointerException("comment is null");
        this.comment = comment;
    }

    public void state(JFormatter f) {
        for (String value : comment.split("[\r\n]")) {
            f.p("// " + value).nl();
        }
    }
}

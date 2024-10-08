package org.metatype.sxc.builder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JVar;
import org.metatype.sxc.builder.impl.IdentityManager;

import javax.xml.namespace.QName;

public interface WriterBuilder {

    /**
     * Get the current object being written.
     * @return
     */
    JVar getObject();
    
    /**
     * Get the XMLStreamWriter.
     * @return
     */
    JVar getXSW();

    JCodeModel getCodeModel();

    JDefinedClass getWriterClass();

    JBlock getCurrentBlock();

    void setCurrentBlock(JBlock block);

    QName getName();

    WriterBuilder getParent();

    /**
     * Call another Method which is represented by this WriterBuilder.
     * @param builder
     */
    void moveTo(WriterBuilder builder);

    /**
     * Write the current object as the specified simple type.
     * @param cls
     */
    void writeAs(Class cls);

    void declareException(Class cls);


    IdentityManager getVariableManager();
}

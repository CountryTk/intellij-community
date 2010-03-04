package com.jetbrains.python.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ArrayFactory;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyImportElement;
import com.jetbrains.python.psi.PyImportStatement;
import com.jetbrains.python.psi.stubs.PyImportStatementStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class PyImportStatementImpl extends PyBaseElementImpl<PyImportStatementStub> implements PyImportStatement {
  public PyImportStatementImpl(ASTNode astNode) {
    super(astNode);
  }

  public PyImportStatementImpl(PyImportStatementStub stub) {
    super(stub, PyElementTypes.IMPORT_STATEMENT);
  }

  @Override
  public boolean processDeclarations(@NotNull final PsiScopeProcessor processor,
                                     @NotNull final ResolveState state,
                                     final PsiElement lastParent,
                                     @NotNull final PsiElement place) {
    // import is per-file
    if (place.getContainingFile() != getContainingFile()) {
      return true;
    }
    for (PyImportElement element : getImportElements()) {
      if (element == lastParent) continue;
      if (!element.processDeclarations(processor, state, null, place)) return false;
    }
    return true;
  }

  public PyImportElement[] getImportElements() {
    final PyImportStatementStub stub = getStub();
    if (stub != null) {
      return stub.getChildrenByType(PyElementTypes.IMPORT_ELEMENT, new ArrayFactory<PyImportElement>() {
        public PyImportElement[] create(int count) {
          return new PyImportElement[count];
        }
      });
    }
    return childrenToPsi(TokenSet.create(PyElementTypes.IMPORT_ELEMENT), new PyImportElement[0]);
  }

  @Override
  protected void acceptPyVisitor(PyElementVisitor pyVisitor) {
    pyVisitor.visitPyImportStatement(this);
  }
}

package com.intellij.history.integration.ui.models;

import com.intellij.history.core.ILocalVcs;
import com.intellij.history.integration.IdeaGateway;
import com.intellij.history.integration.LocalHistoryBundle;
import com.intellij.history.integration.revertion.FileReverter;
import com.intellij.history.integration.revertion.RevisionReverter;
import com.intellij.openapi.diff.DiffContent;
import com.intellij.openapi.diff.DocumentContent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.vfs.VirtualFile;

public class FileHistoryDialogModel extends HistoryDialogModel {
  public FileHistoryDialogModel(IdeaGateway gw, ILocalVcs vcs, VirtualFile f) {
    super(gw, vcs, f);
  }

  public boolean canShowDifference(RevisionProcessingProgress p) {
    p.processingLeftRevision();
    if (getLeftEntry().hasUnavailableContent()) return false;

    p.processingRightRevision();
    return !getRightEntry().hasUnavailableContent();
  }

  public FileDifferenceModel getDifferenceModel() {
    return new EntireFileDifferenceModel(getLeftEntry(), getRightEntry()) {
      @Override
      public String getRightTitle() {
        if (isCurrentRevisionSelected()) return LocalHistoryBundle.message("current.revision");
        return super.getRightTitle();
      }

      @Override
      public DiffContent getRightDiffContent(IdeaGateway gw, EditorFactory ef, RevisionProcessingProgress p) {
        if (isCurrentRevisionSelected()) {
          Document d = gw.getDocumentFor(myFile);
          return DocumentContent.fromDocument(gw.getProject(), d);
        }
        return super.getRightDiffContent(gw, ef, p);
      }
    };
  }

  @Override
  protected RevisionReverter createRevisionReverter() {
    return new FileReverter(myGateway, getLeftRevision(), getLeftEntry(), getRightEntry());
  }
}

package io.github.railroad.editor;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;

import org.apache.commons.io.FilenameUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import io.github.railroad.Railroad;
import io.github.railroad.editor.regex.JavaRegex;
import io.github.railroad.editor.regex.JsonRegex;
import io.github.railroad.objects.RailroadCodeArea;
import io.github.railroad.project.lang.LangProvider;
import javafx.concurrent.Task;

/**
 * @author TurtyWurty
 */
public class CodeEditor {
    public final ExecutorService executor;
    
    public CodeEditor(final ExecutorService service) {
        this.executor = service;
    }
    
    /**
     * @return The default {@link CodeArea}.
     */
    public RailroadCodeArea createCodeArea() {
        final var codeArea = new RailroadCodeArea();
        codeArea.replaceText(0, codeArea.getText().length(), LangProvider.fromLang("editor.loadFileInfo"));
        codeArea.setEditable(false);
        codeArea.prefWidth(400);
        codeArea.prefHeight(600);
        codeArea.setId("codeArea");
        
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500))
            .supplyTask(() -> computeHighlightingAsync(codeArea)).awaitLatest(codeArea.multiPlainChanges())
            .filterMap(t -> {
                if (t.isSuccess())
                    return Optional.of(t.get());
                t.getFailure().printStackTrace();
                return Optional.empty();
            }).subscribe(style -> applyHighlighting(codeArea, style));
        
        new BracketHighlighter(codeArea);
        
        Railroad.resetDiscordPresence();
        
        return codeArea;
    }
    
    /**
     * Sets the styles.
     *
     * @param area         - The {@link CodeArea} to highlight.
     * @param highlighting - The style that is used to highlight it.
     */
    private void applyHighlighting(final CodeArea area, final StyleSpans<Collection<String>> highlighting) {
        area.setStyleSpans(0, highlighting);
    }
    
    /**
     * Computes what should be highlighted with what style using regular
     * expressions.
     *
     * @param  codeArea - The {@link CodeArea} to compute.
     * @return          The created styles.
     */
    private StyleSpans<Collection<String>> computeHighlighting(final RailroadCodeArea codeArea) {
        final String text = codeArea.getText();
        var lastKwEnd = 0;
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        var styleClass = "";
        
        if (codeArea.getFile() == null)
            return spansBuilder.create();
        
        Matcher matcher;
        
        switch (FilenameUtils.getExtension(codeArea.getFile().getName())) {
            case "java":
                matcher = JavaRegex.PATTERN.matcher(text);
                while (matcher.find()) {
                    if (matcher.group("KEYWORD") != null) {
                        styleClass = "java-keyword";
                    } else if (matcher.group("VAR") != null) {
                        styleClass = "java-var";
                    } else if (matcher.group("LITERAL") != null) {
                        styleClass = "java-literal";
                    } else if (matcher.group("CONSTVAR") != null) {
                        styleClass = "java-constvar";
                    } else if (matcher.group("VARIABLE") != null) {
                        styleClass = "java-variable";
                    } else if (matcher.group("PAREN") != null) {
                        styleClass = "java-paren";
                    } else if (matcher.group("BRACE") != null) {
                        styleClass = "java-brace";
                    } else if (matcher.group("BRACKET") != null) {
                        styleClass = "java-bracket";
                    } else if (matcher.group("DIAMOND") != null) {
                        styleClass = "java-diamond";
                    } else if (matcher.group("SEMICOLON") != null) {
                        styleClass = "java-semicolon";
                    } else if (matcher.group("STRING") != null) {
                        styleClass = "java-string";
                    } else if (matcher.group("COMMENT") != null) {
                        styleClass = "java-comment";
                    } else if (matcher.group("ANNOTATION") != null) {
                        styleClass = "java-annotation";
                    } else if (matcher.group("METHOD") != null) {
                        styleClass = "java-method";
                    } else if (matcher.group("GENERICCONSTR") != null) {
                        styleClass = "java-genericconstr";
                    } else if (matcher.group("NUMBER") != null) {
                        styleClass = "java-number";
                    }
                    spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                    spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                    lastKwEnd = matcher.end();
                }
                break;
            case "json":
                matcher = JsonRegex.PATTERN.matcher(text);
                while (matcher.find()) {
                    if (matcher.group("KEY") != null) {
                        styleClass = "json-key";
                    } else if (matcher.group("VALUE") != null) {
                        styleClass = "json-value";
                    } else if (matcher.group("BRACE") != null) {
                        styleClass = "json-brace";
                    } else if (matcher.group("BRACKET") != null) {
                        styleClass = "json-bracket";
                    } else if (matcher.group("LITERAL") != null) {
                        styleClass = "json-literal";
                    }
                    spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                    spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                    lastKwEnd = matcher.end();
                }
                break;
            default:
                return spansBuilder.create();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    /**
     * Asynchronously computes the highlighting.
     *
     * @param  area - The {@link CodeArea} tp compute.
     * @return      The {@link Task} that is used to do the calculation.
     */
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync(final RailroadCodeArea area) {
        final Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(area);
            }
        };
        this.executor.execute(task);
        return task;
    }
}

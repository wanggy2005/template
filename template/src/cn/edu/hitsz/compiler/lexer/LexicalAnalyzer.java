package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String sourceCode;
    private List<Token> tokens;

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        sourceCode = FileUtils.readFile(path);
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程
        
        tokens = new ArrayList<Token>();
        int pos = 0;
        while (pos < sourceCode.length()) {
            char c = sourceCode.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (pos < sourceCode.length() && Character.isLetterOrDigit(sourceCode.charAt(pos))) {
                    sb.append(sourceCode.charAt(pos));
                    pos++;
                }
                String word = sb.toString();

                if (TokenKind.isAllowed(word)) {
                    tokens.add(Token.simple(word));
                } else {
                    if (!symbolTable.has(word)) {
                        symbolTable.add(word);
                    }
                    tokens.add(Token.normal("id", word));
                }
                continue;
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (pos < sourceCode.length() && Character.isDigit(sourceCode.charAt(pos))) {
                    sb.append(sourceCode.charAt(pos));
                    pos++;
                }
                tokens.add(Token.normal("IntConst", sb.toString()));
                continue;
            }

            switch (c) {
                case '+' -> tokens.add(Token.simple("+"));
                case '-' -> tokens.add(Token.simple("-"));
                case '*' -> tokens.add(Token.simple("*"));
                case '/' -> tokens.add(Token.simple("/"));
                case '(' -> tokens.add(Token.simple("("));
                case ')' -> tokens.add(Token.simple(")"));
                case ';' -> tokens.add(Token.simple("Semicolon"));
                case '=' -> tokens.add(Token.simple("="));
                case ',' -> tokens.add(Token.simple(","));
                default -> throw new RuntimeException("Unexpected character: " + c);
            }
            pos++;
        }
        tokens.add(Token.eof());
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokens;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}

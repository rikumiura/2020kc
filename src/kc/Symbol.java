package kc;
enum Symbol {
    NULL,
    MAIN,       /* main */
    IF,         /* if */
    WHILE,      /* while */
	FOR,        /* for */
    INPUTINT,   /* inputint */
    INPUTCHAR,  /* inputchar */
    OUTPUTINT,  /* outputint */
    OUTPUTCHAR, /* outputchar */
    OUTPUTSTR,  /* outputstr(拡張用) */
    SETSTR,     /* setstr   (拡張用) */
    ELSE,       /* else     (拡張用) */
    DO,         /* do       (拡張用) */
    SWITCH,     /* switch   (拡張用) */
    CASE,       /* case     (拡張用) */
    BREAK,      /* break */
    CONTINUE,   /* continue (拡張用) */
    INT,        /* int */
    CHAR,       /* char     (拡張用) */
    BOOLEAN,    /* boolean  (拡張用) */
    TRUE,       /* true     (拡張用) */
    FALSE,      /* false    (拡張用) */
    EQUAL,      /* == */ //OK
    NOTEQ,      /* != */ //OK
    LESS,       /* < */ //OK
    GREAT,      /* > */ //OK
    LESSEQ,     /* <=       (拡張用) */ //OK
    GREATEQ,    /* >=       (拡張用) */ //OK
    AND,        /* && */ //OK
    OR,         /* || */ //OK
    NOT,        /* ! */ //OK
    ADD,        /* + */ //OK
    SUB,        /* - */ //OK
    MUL,        /* * */ //OK
    DIV,        /* / */ //OK
    MOD,        /* % */ //OK
    ASSIGN,     /* = */ //OK
    ASSIGNADD,  /* += */ //OK
    ASSIGNSUB,  /* -= */ //OK
    ASSIGNMUL,  /* *= */ //OK
    ASSIGNDIV,  /* /= */ //OK
    ASSIGNMOD,  /* %=       (拡張用) */ //OK
    INC,        /* ++ */ //OK
    DEC,        /* -- */ //OK
    SEMICOLON,  /* ; */ //OK
    LPAREN,     /* ( */ //OK
    RPAREN,     /* ) */ //OK
    LBRACE,     /* { */ //OK
    RBRACE,     /* } */ //OK
    LBRACKET,   /* [ */ //OK
    RBRACKET,   /* ] */ //OK
    COMMA,      /* , */ //OK
    INTEGER,    /* 整数 */
    CHARACTER,  /* 文字 */
    NAME,       /* 変数名 */
    STRING,     /* 文字列   (拡張用) */
    ERR,        /* エラー */
    EOF         /* end of file */
}
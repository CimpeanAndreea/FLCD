%{
#include <stdio.h>
#include <stdlib.h>

#define YYDEBUG 1
%}

%token MAIN
%token DECLARATIONS
%token STATEMENTS
%token INTEGER
%token STRING
%token BOOLEAN
%token CHARACTER
%token ARRAY
%token CONST
%token AND
%token OR
%token IN
%token OUT
%token IF
%token ELSE
%token FOR
%token WHILE
%token DO

%token SEMICOLON
%token COLON
%token COMMA
%token OPEN_CURLY_BRACKET
%token CLOSED_CURLY_BRACKET
%token OPEN_ROUND_BRACKET
%token CLOSED_ROUND_BRACKET
%token OPEN_SQUARE_BRACKET
%token CLOSED_SQUARE_BRACKET

%token IDENTIFIER
%token INTEGER_CONST
%token STRING_CONST
%token CHARACTER_CONST
%token BOOLEAN_CONST

%token OPERATOR
%token IN_OPERATOR
%token OUT_OPERATOR
%token COMPARISON_OPERATOR
%token ASSIGN_OPERATOR
%token FOLLOW_OPERATOR

%token PROGRAM

%%
program :       PROGRAM MAIN FOLLOW_OPERATOR OPEN_CURLY_BRACKET DECLARATIONS declarations_list STATEMENTS statements_list CLOSED_CURLY_BRACKET  { printf("program\n"); }
        ;

declarations_list : declaration declarations_list       { printf("declarations_list -> declaration declarations_list\n"); }
                  |
                  ;
declaration :   simple_declaration SEMICOLON            { printf("declaration -> simple_declaration\n"); }
            | array_declaration SEMICOLON               { printf("declaration -> array_declaration\n"); }
            | const_declaration SEMICOLON               { printf("declaration -> const_declaration\n"); }
            ;
simple_declaration :    simple_type COLON list_identifiers      { printf("simple_declaration -> list_identifiers\n"); }
                   ;
simple_type :   CHARACTER       { printf("simple_type -> character\n"); }
            | INTEGER           { printf("simple_type -> integer\n"); }
            | BOOLEAN           { printf("simple_type -> boolean\n"); }
            | STRING            { printf("sime_type -> string\n"); }
            ;
list_identifiers :       IDENTIFIER     { printf("list_identifiers -> identifier\n"); }
                 | initialized_identifier       { printf("list_identifiers -> initialized_identifier\n"); }
                 | IDENTIFIER COMMA list_identifiers    { printf("list_identifiers -> identifier list_identifiers\n"); }
                 | initialized_identifier COMMA list_identifiers        { printf("list_identifiers -> initialized_identifier list_identifiers\n"); }
                 ;
initialized_identifier :        IDENTIFIER ASSIGN_OPERATOR constant     { printf("initialized_identifier -> identifier constant\n"); }
                       ;
constant :      INTEGER_CONST   { printf("constant -> integer_const\n"); }
         | CHARACTER_CONST      { printf("constant -> character_const\n"); }
         | STRING_CONST         { printf("constant -> string_const\n"); }
         | BOOLEAN_CONST        { printf("constant -> boolean_const\n"); }
         ;

const_declaration :     simple_type CONST COLON list_const_identifiers  { printf("const_declaration -> simple_type list_const_identifiers \n"); }
                  ;

list_const_identifiers :        initialized_const       { printf("list_const_identifiers -> initialized_const\n"); }
                       | initialized_const COMMA list_const_identifiers { printf("list_const_identifiers -> initialized_const list_const_identifiers\n"); }
                       ;
initialized_const :     IDENTIFIER ASSIGN_OPERATOR constant     { printf(" initialized_const -> identifier constant\n"); }
                  ;
array_declaration :     ARRAY OPEN_SQUARE_BRACKET simple_type CLOSED_SQUARE_BRACKET COLON list_array_identifiers { printf("array_declaration\n"); }
                  ;
list_array_identifiers :        array_identifier        { printf("list_array_identifiers -> array_identifier\n"); }
                       | array_identifier COMMA list_array_identifiers { printf("list_array_identifiers -> array_identifier list_array_identifiers\n"); }
                       ;
array_identifier :      IDENTIFIER OPEN_SQUARE_BRACKET IDENTIFIER CLOSED_SQUARE_BRACKET { printf("array_identifier -> identifier identifier\n"); }
                 | IDENTIFIER OPEN_SQUARE_BRACKET INTEGER_CONST CLOSED_SQUARE_BRACKET { printf("array_identifier -> identifier constant"); }
                 ;
statements_list :       statement       { printf("statements_list -> statement\n"); }
                | compound_statement { printf("statements_list -> compound_statement\n"); }
                ;
compound_statement :    OPEN_CURLY_BRACKET statement_lst CLOSED_CURLY_BRACKET { printf("compund_statement -> statement\n"); }
                   ;
statement_lst : statement statement_lst { printf("statement_lst\n"); }
              |
            ;

statement :      simple_statement       { printf("statement -> simple_statement\n"); }
          | struct_statement            { printf("statement -> struct_statement\n"); }
          ;
simple_statement :      assign_statement SEMICOLON      { printf("simple_statement -> assign_statement\n"); }
                 | read_statement SEMICOLON             { printf("simple_statement -> read_statement\n"); }
                 | write_statement SEMICOLON            { printf("simple_statement -> write_statement\n"); }
                 ;
assign_statement :      IDENTIFIER ASSIGN_OPERATOR expression   { printf("assign_statement -> identifier\n"); }
                 | array_element ASSIGN_OPERATOR expression     { printf("assign_statement -> array_element\n"); }
                 ;
expression :    constant        { printf("expression -> constant\n"); }
           | IDENTIFIER         { printf("expression -> identifier\n"); }
           | array_element      { printf("expression -> array_element\n"); }
           | expression OPERATOR expression     { printf("expression -> expression operator expression\n"); }
           | OPEN_ROUND_BRACKET expression OPEN_ROUND_BRACKET   { printf("(expression)\n"); }
           ;
array_element :         IDENTIFIER OPEN_SQUARE_BRACKET IDENTIFIER CLOSED_SQUARE_BRACKET { printf("array_element -> identifier identifier\n"); }
              | IDENTIFIER OPEN_SQUARE_BRACKET constant CLOSED_SQUARE_BRACKET { printf("array_element -> identifier constaant\n"); }
              ;
read_statement :        IN IN_OPERATOR list_identifiers_in      { printf("read_statement list_identifiers_in\n"); }
               | IN OPEN_ROUND_BRACKET STRING_CONST CLOSED_ROUND_BRACKET IN_OPERATOR list_identifiers_in { printf("read_statement + message\n"); }
               ;
list_identifiers_in :   IDENTIFIER      { printf("list_identifiers_in -> identifier\n"); }
                    | IDENTIFIER COMMA list_identifiers_in      { printf("list_identifiers_in -> identifier list_identifier_in\n"); }
                    ;
write_statement :       OUT OUT_OPERATOR list_outputs   { printf("write_statement -> list_outputs\n"); }
               ;
list_outputs :  expression                      { printf("list_outputs -> expression\n"); }
             | expression COMMA list_outputs    { printf("list_outputs -> expression list_outputs\n"); }
             ;
struct_statement :      if_statement            { printf("struct_statement -> if_statement\n"); }
                 | while_statement              { printf("struct_statement -> while_statement\n"); }
                 | for_statement                { printf("struct_statement -> for_statement\n"); }
                 ;
if_statement :  IF OPEN_ROUND_BRACKET condition CLOSED_ROUND_BRACKET statements_list    { printf("if_statement\n"); }
             | IF OPEN_ROUND_BRACKET condition CLOSED_ROUND_BRACKET statements_list ELSE statements_list { printf("if else statement\n"); }
             ;
while_statement : WHILE OPEN_ROUND_BRACKET condition CLOSED_ROUND_BRACKET DO statements_list    { printf("while_statement\n"); }
                ;
for_statement :         FOR OPEN_ROUND_BRACKET assign_statement SEMICOLON condition SEMICOLON assign_statement CLOSED_ROUND_BRACKET statements_list     { printf("for_statement\n"); }
              ;
condition :     expression relation expression  { printf("condition\n"); }
          ;
relation :      COMPARISON_OPERATOR     { printf(" relation -> comparison_operator\n"); }
         | AND                          { printf(" relation -> and \n"); }
         | OR                           { printf(" relation -> or\n"); }
         ;

%%

yyerror(char *s)
{
  printf("%s\n", s);
}

extern FILE *yyin;

main(int argc, char **argv)
{
  if(argc>1) yyin = fopen(argv[1], "r");
  if(argc>2&&!strcmp(argv[2],"-d")) yydebug = 1;
  if(!yyparse()) fprintf(stderr,"\tO.K.\n");
}
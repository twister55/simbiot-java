export interface Position {
    line: number;
    column: number;
    offset: number;
}

export interface SourceLocation {
    start: Position;
    end: Position;
    source?: string | null;
}

export interface Node {
    type: string;
    start?: number;
    end?: number;
    loc?: SourceLocation;
    sourceFile?: string;
}

// AST types for JavaScript-like expressions
export type JSNode = Node;

export type ArgumentListElement = Expression | SpreadElement;
export type ArrayPatternElement = Expression | null;
export type Pattern = ArrayPattern | ObjectPattern | Identifier;
export type Expression = ArrayExpression | ArrowFunctionExpression | AssignmentExpression
    | BinaryExpression | LogicalExpression | CallExpression | MemberExpression | ConditionalExpression
    | Identifier | Literal | ThisExpression | ObjectExpression | RegExpLiteral | SequenceExpression
    | UnaryExpression | UpdateExpression | FunctionDeclaration | ArrowFunctionExpression
    | AssignmentPattern | SpreadElement | TemplateLiteral | TaggedTemplateExpression
    | ENDGetter | ENDCaller | ENDFilter;
export type Statement = ReturnStatement | EmptyStatement | ExpressionStatement;
export type PropertyKey = Identifier | Literal;
export type PropertyValue = Pattern | Literal;
export type LiteralValue = boolean | number | string | null;

export interface Function extends JSNode {
    id: Identifier | null;
    params: Pattern[];
    body: Expression | BlockStatement;
    generator?: boolean;
}

export interface Program extends JSNode {
    type: 'Program';
    raw: string;
    body: Statement[];
}

export interface Literal extends JSNode {
    type: 'Literal';
    value: LiteralValue;
    raw?: string;
}

export type IdentifierContext = 'property' | 'state' | 'variable' | 'store' | 'store-host' | 'helper' | 'definition' | 'argument';

export interface Identifier extends JSNode {
    type: 'Identifier';
    name: string;

    // Endorphin extension: identifier context
    context?: IdentifierContext;
    raw?: string;
}

export interface ThisExpression extends JSNode {
    type: 'ThisExpression';
}

export interface FunctionDeclaration extends Function {
    type: 'FunctionDeclaration';
    id: Identifier;
}

export interface ArrowFunctionExpression extends JSNode {
    type: 'ArrowFunctionExpression';
    params: Pattern[];
    body: Expression | BlockStatement;
    generator?: boolean;
    expression?: boolean;
    async?: boolean;
}

export interface AssignmentPattern extends JSNode {
    type: 'AssignmentPattern';
    left: Pattern;
    right: Expression;
}

export interface ObjectPattern extends JSNode {
    type: 'ObjectPattern';
    properties: Property[];
}

export interface ArrayPattern extends JSNode {
    type: 'ArrayPattern';
    elements: Pattern[];
}

export interface SpreadElement extends JSNode {
    type: 'SpreadElement';
    argument: Expression;
}

export interface RestElement extends JSNode {
    type: 'RestElement';
    argument: Pattern;
}

export interface ArrayExpression extends JSNode {
    type: 'ArrayExpression';
    elements: ArrayPatternElement[];
}

export interface ObjectExpression extends JSNode {
    type: 'ObjectExpression';
    properties: Property[];
}

export interface Property extends JSNode {
    type: 'Property';
    kind: 'init' | 'get' | 'set';
    key: PropertyKey;
    value: Expression;
    computed?: boolean;
    method?: boolean;
    shorthand?: boolean;
}

export interface BaseExpression extends JSNode {
    left: Expression;
    operator: string;
    right: Expression;
}

export interface AssignmentExpression extends BaseExpression {
    type: 'AssignmentExpression';
}

export interface BinaryExpression extends BaseExpression {
    type: 'BinaryExpression';
}

export interface LogicalExpression extends BaseExpression {
    type: 'LogicalExpression';
}

export interface CallExpression extends JSNode {
    type: 'CallExpression';
    callee: Expression;
    arguments: ArgumentListElement[];
}

export interface MemberExpression extends JSNode {
    type: 'MemberExpression';
    object: Expression;
    property: Expression;
    computed?: boolean;
}

export interface ConditionalExpression extends JSNode {
    type: 'ConditionalExpression';
    test: Expression;
    consequent: Expression;
    alternate: Expression;
}

export interface RegExpLiteral extends JSNode {
    type: 'RegExpLiteral';
    regex: { pattern: string, flags: string };
}

export interface SequenceExpression extends JSNode {
    type: 'SequenceExpression';
    expressions: Expression[];
}

export interface UnaryExpression extends JSNode {
    type: 'UnaryExpression';
    prefix?: boolean;
    operator: string;
    argument: Expression;
}

export interface UpdateExpression extends JSNode {
    type: 'UpdateExpression';
    operator: string;
    argument: Expression;
    prefix: boolean;
}

export interface ExpressionStatement extends JSNode {
    type: 'ExpressionStatement';
    expression: Expression;
}

export interface EmptyStatement extends JSNode {
    type: 'EmptyStatement';
}

export interface ReturnStatement extends JSNode {
    type: 'ReturnStatement';
    argument: Expression | null;
}

export interface BlockStatement extends JSNode {
    type: 'BlockStatement';
    body: Statement[];
}

export interface TemplateLiteral extends JSNode {
    type: 'TemplateLiteral';
    quasis: TemplateElement[];
    expressions: Expression[];
}

export interface TaggedTemplateExpression extends JSNode {
    type: 'TaggedTemplateExpression';
    tag: Expression;
    quasi: TemplateLiteral;
}

export interface TemplateElement extends JSNode {
    type: 'TemplateElement';
    tail: boolean;
    value: {
        cooked: string;
        raw: string;
    };
}

// Endorphin template AST
export type ENDNode = Node;

export type ENDStatement = ENDElement | ENDInnerHTML | ENDPlainStatement
    | ENDAttributeStatement | ENDAddClassStatement | ENDVariableStatement
    | ENDControlStatement | ENDPartialStatement;
export type ENDProgramStatement = ENDTemplate | ENDPartial | ENDImport | ENDStatement;
export type ENDControlStatement = ENDIfStatement | ENDChooseStatement | ENDForEachStatement | ENDPartialStatement;
export type ENDPlainStatement = Literal | Program;
export type ENDAttributeName = Identifier | Program;
export type ENDBaseAttributeValue = Literal | Program;

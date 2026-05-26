// Generated from com/polydes/datastruct/grammar/Condition.g4 by ANTLR 4.5

    package com.polydes.datastruct.grammar;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConditionParser}.
 */
public interface ConditionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code ExprLe}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprLe(ConditionParser.ExprLeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprLe}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprLe(ConditionParser.ExprLeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprNotEqual}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprNotEqual(ConditionParser.ExprNotEqualContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprNotEqual}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprNotEqual(ConditionParser.ExprNotEqualContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprField}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprField(ConditionParser.ExprFieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprField}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprField(ConditionParser.ExprFieldContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprNot}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprNot(ConditionParser.ExprNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprNot}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprNot(ConditionParser.ExprNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprGt}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprGt(ConditionParser.ExprGtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprGt}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprGt(ConditionParser.ExprGtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprPrimary}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprPrimary(ConditionParser.ExprPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprPrimary}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprPrimary(ConditionParser.ExprPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprAnd}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprAnd(ConditionParser.ExprAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprAnd}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprAnd(ConditionParser.ExprAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprLt}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprLt(ConditionParser.ExprLtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprLt}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprLt(ConditionParser.ExprLtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprInvocation}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprInvocation(ConditionParser.ExprInvocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprInvocation}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprInvocation(ConditionParser.ExprInvocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprNegate}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprNegate(ConditionParser.ExprNegateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprNegate}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprNegate(ConditionParser.ExprNegateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprOr}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprOr(ConditionParser.ExprOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprOr}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprOr(ConditionParser.ExprOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprSub}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprSub(ConditionParser.ExprSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprSub}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprSub(ConditionParser.ExprSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprMultiply}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprMultiply(ConditionParser.ExprMultiplyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprMultiply}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprMultiply(ConditionParser.ExprMultiplyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprGe}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprGe(ConditionParser.ExprGeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprGe}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprGe(ConditionParser.ExprGeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprEqual}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprEqual(ConditionParser.ExprEqualContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprEqual}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprEqual(ConditionParser.ExprEqualContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprAdd}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprAdd(ConditionParser.ExprAddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprAdd}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprAdd(ConditionParser.ExprAddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprDivide}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprDivide(ConditionParser.ExprDivideContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprDivide}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprDivide(ConditionParser.ExprDivideContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprMod}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprMod(ConditionParser.ExprModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprMod}
	 * labeled alternative in {@link ConditionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprMod(ConditionParser.ExprModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimExpr}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimExpr(ConditionParser.PrimExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimExpr}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimExpr(ConditionParser.PrimExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimThis}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimThis(ConditionParser.PrimThisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimThis}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimThis(ConditionParser.PrimThisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimLit}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimLit(ConditionParser.PrimLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimLit}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimLit(ConditionParser.PrimLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimIdent}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimIdent(ConditionParser.PrimIdentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimIdent}
	 * labeled alternative in {@link ConditionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimIdent(ConditionParser.PrimIdentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralInt}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteralInt(ConditionParser.LiteralIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralInt}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteralInt(ConditionParser.LiteralIntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralFloat}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteralFloat(ConditionParser.LiteralFloatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralFloat}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteralFloat(ConditionParser.LiteralFloatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralString}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteralString(ConditionParser.LiteralStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralString}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteralString(ConditionParser.LiteralStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralBool}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteralBool(ConditionParser.LiteralBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralBool}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteralBool(ConditionParser.LiteralBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralNull}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteralNull(ConditionParser.LiteralNullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralNull}
	 * labeled alternative in {@link ConditionParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteralNull(ConditionParser.LiteralNullContext ctx);
}
package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.mixininterface.IStarscript;
import meteordevelopment.starscript.Instruction;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.utils.Stack;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@Mixin(value = Starscript.class, remap = false)
public abstract class StarscriptMixin implements IStarscript {
    @Shadow @Final private Stack<Value> stack;
    @Shadow public abstract void push(Value value);
    @Shadow public abstract Value pop();
    @Shadow public abstract void error(String format, Object... args);
    @Shadow @Final private ValueMap globals;
    @Shadow public abstract Value peek();
    @Shadow public abstract Value peek(int offset);

    @Override
    public String tokyo$run_rawOutput(Script script, StringBuilder sb) {
        stack.clear();

        sb.setLength(0);
        int ip = 0;

        loop:
        while (true) {
            switch (Instruction.valueOf(script.code[ip++])) {
                case Constant:          push(script.constants.get(script.code[ip++])); break;
                case Null:              push(Value.null_()); break;
                case True:              push(Value.bool(true)); break;
                case False:             push(Value.bool(false)); break;

                case Add:               { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(a.getNumber() + b.getNumber())); else if (a.isString()) push(Value.string(a.getString() + b.toString())); else error("Can only add 2 numbers or 1 string and other value."); break; }
                case Subtract:          { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(a.getNumber() - b.getNumber())); else error("Can only subtract 2 numbers."); break; }
                case Multiply:          { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(a.getNumber() * b.getNumber())); else error("Can only multiply 2 numbers."); break; }
                case Divide:            { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(a.getNumber() / b.getNumber())); else error("Can only divide 2 numbers."); break; }
                case Modulo:            { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(a.getNumber() % b.getNumber())); else error("Can only modulo 2 numbers."); break; }
                case Power:             { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(Math.pow(a.getNumber(), b.getNumber()))); else error("Can only power 2 numbers."); break; }

                case AddConstant:       { Value b = script.constants.get(script.code[ip++]); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.number(a.getNumber() + b.getNumber())); else if (a.isString()) push(Value.string(a.getString() + b.toString())); else error("Can only add 2 numbers or 1 string and other value."); break; }

                case Pop:               pop(); break;
                case Not:               push(Value.bool(!pop().isTruthy())); break;
                case Negate:            { Value a = pop(); if (a.isNumber()) push(Value.number(-a.getNumber())); else error("This operation requires a number."); break; }

                case Equals:            push(Value.bool(pop().equals(pop()))); break;
                case NotEquals:         push(Value.bool(!pop().equals(pop()))); break;
                case Greater:           { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.bool(a.getNumber() > b.getNumber())); else error("This operation requires 2 number."); break; }
                case GreaterEqual:      { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.bool(a.getNumber() >= b.getNumber())); else error("This operation requires 2 number."); break; }
                case Less:              { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.bool(a.getNumber() < b.getNumber())); else error("This operation requires 2 number."); break; }
                case LessEqual:         { Value b = pop(); Value a = pop(); if (a.isNumber() && b.isNumber()) push(Value.bool(a.getNumber() <= b.getNumber())); else error("This operation requires 2 number."); break; }

                case Variable:          { String name = script.constants.get(script.code[ip++]).getString(); Supplier<Value> s = globals.get(name); push(s != null ? s.get() : Value.null_()); break; }
                case Get:               { String name = script.constants.get(script.code[ip++]).getString(); Value v = pop(); if (!v.isMap()) { push(Value.null_()); break; } Supplier<Value> s = v.getMap().get(name); push(s != null ? s.get() : Value.null_()); break; }
                case Call:              { int argCount = script.code[ip++]; Value a = peek(argCount); if (a.isFunction()) { Value r = a.getFunction().run((Starscript) (Object) this, argCount); pop(); push(r); } else error("Tried to call a %s, can only call functions.", a.type); break; }

                case Jump:              { int jump = ((script.code[ip++] << 8) & 0xFF) | (script.code[ip++] & 0xFF); ip += jump; break; }
                case JumpIfTrue:        { int jump = ((script.code[ip++] << 8) & 0xFF) | (script.code[ip++] & 0xFF); if (peek().isTruthy()) ip += jump; break; }
                case JumpIfFalse:       { int jump = ((script.code[ip++] << 8) & 0xFF) | (script.code[ip++] & 0xFF); if (!peek().isTruthy()) ip += jump; break; }

                case Section:           ip++; break;

                case Append:            sb.append(pop().toString()); break;
                case ConstantAppend:    sb.append(script.constants.get(script.code[ip++]).toString()); break;
                case VariableAppend:    { Supplier<Value> s = globals.get(script.constants.get(script.code[ip++]).getString()); sb.append((s == null ? Value.null_() : s.get()).toString()); break; }
                case GetAppend:         { String name = script.constants.get(script.code[ip++]).getString(); Value v = pop(); if (!v.isMap()) { sb.append(Value.null_()); break; } Supplier<Value> s = v.getMap().get(name); sb.append((s != null ? s.get() : Value.null_()).toString()); break; }
                case CallAppend:        { int argCount = script.code[ip++]; Value a = peek(argCount); if (a.isFunction()) { Value r = a.getFunction().run((Starscript) (Object) this, argCount); pop(); sb.append(r.toString()); } else error("Tried to call a %s, can only call functions.", a.type); break; }

                case VariableGet:       {
                    Value v;
                    { String name = script.constants.get(script.code[ip++]).getString(); Supplier<Value> s = globals.get(name); v = s != null ? s.get() : Value.null_(); } // Variable
                    { String name = script.constants.get(script.code[ip++]).getString(); if (!v.isMap()) { push(Value.null_()); break; } Supplier<Value> s = v.getMap().get(name); push(s != null ? s.get() : Value.null_()); } // Get
                    break;
                }
                case VariableGetAppend: {
                    Value v;
                    { String name = script.constants.get(script.code[ip++]).getString(); Supplier<Value> s = globals.get(name); v = s != null ? s.get() : Value.null_(); } // Variable
                    { String name = script.constants.get(script.code[ip++]).getString(); if (!v.isMap()) { push(Value.null_()); break; } Supplier<Value> s = v.getMap().get(name); v = s != null ? s.get() : Value.null_(); } // Get
                    { sb.append(v.toString()); } // Append
                    break;
                }

                case End:               break loop;
                default:                throw new UnsupportedOperationException("Unknown instruction '" + Instruction.valueOf(script.code[ip]) + "'");
            }
        }

        return sb.toString();
    }
}

package net.danielmaly.scheme.eval;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import net.danielmaly.scheme.builtin.BuiltinExpression;
import net.danielmaly.scheme.builtin.conversion.StringToBooleanFactory;
import net.danielmaly.scheme.builtin.conversion.StringToNumber;
import net.danielmaly.scheme.builtin.conversion.StringToNumberFactory;
import net.danielmaly.scheme.builtin.conversion.ToStringFactory;
import net.danielmaly.scheme.builtin.io.ReadFactory;
import net.danielmaly.scheme.builtin.list.*;
import net.danielmaly.scheme.builtin.predicates.*;
import net.danielmaly.scheme.builtin.arithmetic.*;
import net.danielmaly.scheme.builtin.io.DisplayFactory;
import net.danielmaly.scheme.builtin.io.NewlineFactory;
import net.danielmaly.scheme.builtin.ordering.GreaterThanEqualFactory;
import net.danielmaly.scheme.builtin.ordering.GreaterThanFactory;
import net.danielmaly.scheme.builtin.ordering.LessThanEqualFactory;
import net.danielmaly.scheme.builtin.ordering.LessThanFactory;
import net.danielmaly.scheme.eval.symbols.ReadFunctionArgument;
import net.danielmaly.scheme.types.SchemeFunction;

public class SchemeContext {
    private final FrameDescriptor globalFrameDescriptor;
    private final VirtualFrame globalFrame;
    private final MaterializedFrame globalMaterializedFrame;

    public SchemeContext() {
        this.globalFrameDescriptor = new FrameDescriptor();
        this.globalFrame = this.initGlobalFrame();
        this.globalMaterializedFrame = this.globalFrame.materialize();
    }

    private VirtualFrame initGlobalFrame() {
        VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(null, this.globalFrameDescriptor);
        addGlobalFunctions(frame);
        return frame;
    }

    private static void addGlobalFunctions(VirtualFrame frame) {
        FrameDescriptor fd = frame.getFrameDescriptor();

        addToFrame(frame, fd, "/", DividedByFactory.getInstance());
        addToFrame(frame, fd, "-", MinusFactory.getInstance());
        addToFrame(frame, fd, "+", PlusFactory.getInstance());
        addToFrame(frame, fd, "*", TimesFactory.getInstance());
        addToFrame(frame, fd, "%", ModuloFactory.getInstance());

        addToFrame(frame, fd, "display", DisplayFactory.getInstance());
        addToFrame(frame, fd, "newline", NewlineFactory.getInstance());
        addToFrame(frame, fd, "read", ReadFactory.getInstance());

        addToFrame(frame, fd, "string", ToStringFactory.getInstance());
        addToFrame(frame, fd, "string->number", StringToNumberFactory.getInstance());
        addToFrame(frame, fd, "string->boolean", StringToBooleanFactory.getInstance());

        addToFrame(frame, fd, ">", GreaterThanFactory.getInstance());
        addToFrame(frame, fd, ">=", GreaterThanEqualFactory.getInstance());
        addToFrame(frame, fd, "<", LessThanFactory.getInstance());
        addToFrame(frame, fd, "<=", LessThanEqualFactory.getInstance());

        addToFrame(frame, fd, "=", EqualNumericFactory.getInstance());
        addToFrame(frame, fd, "eq?", EqFactory.getInstance());
        addToFrame(frame, fd, "eqv?", EqvFactory.getInstance());
        addToFrame(frame, fd, "equal?", EqualFactory.getInstance());
        addToFrame(frame, fd, "not", NotFactory.getInstance());

        addToFrame(frame, fd, "boolean?", IsBooleanFactory.getInstance());
        addToFrame(frame, fd, "null?", IsNullFactory.getInstance());
        addToFrame(frame, fd, "procedure?", IsProcedureFactory.getInstance());
        addToFrame(frame, fd, "zero?", IsZeroFactory.getInstance());
        addToFrame(frame, fd, "number?", IsNumberFactory.getInstance());
        addToFrame(frame, fd, "string?", IsStringFactory.getInstance());

        addToFrame(frame, fd, "cons", ConsFactory.getInstance());
        addToFrame(frame, fd, "list", new ListFunction());
        addToFrame(frame, fd, "car", CarFactory.getInstance());
        addToFrame(frame, fd, "cdr", CdrFactory.getInstance());
        addToFrame(frame, fd, "length", LengthFactory.getInstance());
        addToFrame(frame, fd, "append", AppendFactory.getInstance());
        addToFrame(frame, fd, "reverse", ReverseFactory.getInstance());
        addToFrame(frame, fd, "map", MapFactory.getInstance());

    }

    private static void addToFrame(VirtualFrame frame,
                                   FrameDescriptor frameDescriptor,
                                   String identifier,
                                   NodeFactory<? extends BuiltinExpression> factory) {

        frame.setObject(
                frameDescriptor.addFrameSlot(identifier, FrameSlotKind.Object),
                createBuiltinFunction(factory, frameDescriptor)
        );

    }

    private static void addToFrame(VirtualFrame frame,
                                   FrameDescriptor frameDescriptor,
                                   String identifier,
                                   BuiltinExpression expression) {

        frame.setObject(
                frameDescriptor.addFrameSlot(identifier, FrameSlotKind.Object),
                createBuiltinFunctionDirect(expression, frameDescriptor)
        );

    }

    /**
     * @return A {@link MaterializedFrame} on the heap that contains all global
     * values.
     */
    public MaterializedFrame getMaterializedGlobalFrame() {
        return this.globalMaterializedFrame;
    }

    public VirtualFrame getGlobalFrame() {
        return globalFrame;
    }

    public FrameDescriptor getGlobalFrameDescriptor() {
        return globalFrameDescriptor;
    }

    public static SchemeFunction createBuiltinFunction(
            NodeFactory<? extends BuiltinExpression> factory,
            FrameDescriptor frameDescriptor)
    {
        int argumentCount = factory.getExecutionSignature().size();
        SchemeExpression[] argumentNodes = new SchemeExpression[argumentCount];
        for (int i=0; i<argumentCount; i++) {
            argumentNodes[i] = new ReadFunctionArgument(i);
        }
        BuiltinExpression node = factory.createNode((Object) argumentNodes);
        return new SchemeFunction(Truffle.getRuntime().createCallTarget(
                new SchemeRootNode(new SchemeExpression[] {node}, frameDescriptor)));
    }

    public static SchemeFunction createBuiltinFunctionDirect(BuiltinExpression expression, FrameDescriptor frameDescriptor) {
        return new SchemeFunction(Truffle.getRuntime().createCallTarget(
                new SchemeRootNode(new SchemeExpression[] {expression}, frameDescriptor)
        ));
    }


}

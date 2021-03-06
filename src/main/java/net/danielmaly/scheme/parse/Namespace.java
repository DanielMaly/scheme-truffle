package net.danielmaly.scheme.parse;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import net.danielmaly.scheme.eval.GenericPair;

public class Namespace {
    public static final int LEVEL_GLOBAL = -1;
    public static final int LEVEL_UNDEFINED = -2;

    /**
     * The name for the namespace at the top level of a file.
     */
    public static final String TOP_NS = "<top>";

    /**
     * The name of the global namespace that contains all predefined variables.
     */
    private static final String GLOBAL_NS = "<global>";

    private final String functionName;
    private final Namespace parent;
    private final FrameDescriptor frameDescriptor;

    public Namespace(FrameDescriptor frameDescriptor) {
        this.functionName = GLOBAL_NS;
        this.parent = null;
        this.frameDescriptor = frameDescriptor;
    }

    public Namespace(String name, Namespace parent) {
        this.functionName = name;
        this.parent = parent;
        this.frameDescriptor = new FrameDescriptor();
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public Namespace getParent() {
        return this.parent;
    }

    public FrameDescriptor getFrameDescriptor() {
        return this.frameDescriptor;
    }

    public FrameSlot addIdentifier(String id) {
        return this.frameDescriptor.addFrameSlot(id, FrameSlotKind.Object);
    }

    public GenericPair<Integer, FrameSlot> getOrCreateIdentifier(String id) {
        GenericPair<Integer, FrameSlot> identifier = getIdentifier(id);
        if(identifier.snd != null) {
            return identifier;
        }
        else {
            FrameSlot slot = this.frameDescriptor.addFrameSlot(id);
            return new GenericPair<>(0, slot);
        }
    }

    public GenericPair<Integer, FrameSlot> getIdentifier(String id) {
        int depth = 0;
        Namespace current = this;
        FrameSlot slot = current.frameDescriptor.findFrameSlot(id);
        while (slot == null) {
            depth++;
            current = current.parent;
            if (current == null) {
                return new GenericPair<>(LEVEL_UNDEFINED, null);
            }
            slot = current.frameDescriptor.findFrameSlot(id);
        }
        if (current.parent == null) {
            return new GenericPair<>(LEVEL_GLOBAL, slot);
        }
        return new GenericPair<>(depth, slot);
    }
}
package net.danielmaly.scheme.eval.symbols;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class GlobalVariable extends Variable {

    public abstract MaterializedFrame getGlobalFrame();

    @Specialization(rewriteOn = FrameSlotTypeException.class)
    protected long readLong(VirtualFrame virtualFrame)
            throws FrameSlotTypeException {
        return this.getGlobalFrame().getLong(this.getSlot());
    }

    @Specialization(rewriteOn = FrameSlotTypeException.class)
    protected double readDouble(VirtualFrame virtualFrame) throws FrameSlotTypeException {
        return this.getGlobalFrame().getDouble(this.getSlot());
    }

    @Specialization(rewriteOn = FrameSlotTypeException.class)
    protected boolean readBoolean(VirtualFrame virtualFrame)
            throws FrameSlotTypeException {
        return this.getGlobalFrame().getBoolean(this.getSlot());
    }

    @Specialization(rewriteOn = FrameSlotTypeException.class)
    protected Object readObject(VirtualFrame virtualFrame)
            throws FrameSlotTypeException {
        return this.getGlobalFrame().getObject(this.getSlot());
    }

    @Specialization(contains = { "readLong", "readDouble", "readBoolean", "readObject", })
    protected Object read(VirtualFrame virtualFrame) {
        return this.getGlobalFrame().getValue(this.getSlot());
    }
}
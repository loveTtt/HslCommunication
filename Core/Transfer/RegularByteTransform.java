/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Transfer;

import HslCommunication.Core.Transfer.ByteTransformBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.IByteTransform;

public class RegularByteTransform
extends ByteTransformBase {
    public RegularByteTransform() {
    }

    public RegularByteTransform(DataFormat dataFormat) {
        super(dataFormat);
    }

    @Override
    public IByteTransform CreateByDateFormat(DataFormat dataFormat) {
        RegularByteTransform transform = new RegularByteTransform(dataFormat);
        transform.setIsStringReverse(this.getIsStringReverse());
        return transform;
    }
}


package org.seemse.common.core.exception.file;

import java.io.Serial;

/**
 * 文件名称超长限制异常类
 *
 * @author seemse
 */
public class FileNameLengthLimitExceededException extends FileException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super("upload.filename.exceed.length", new Object[]{defaultFileNameLength});
    }
}

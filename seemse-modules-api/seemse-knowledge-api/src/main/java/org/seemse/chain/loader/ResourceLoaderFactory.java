package org.seemse.chain.loader;

import lombok.AllArgsConstructor;
import org.seemse.chain.split.*;

import org.seemse.config.properties.PdfProperties;
import org.seemse.chain.split.CharacterTextSplitter;
import org.seemse.chain.split.CodeTextSplitter;
import org.seemse.chain.split.MarkdownTextSplitter;
import org.seemse.constant.FileType;
import org.seemse.system.mapper.SysOssMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ResourceLoaderFactory {
    private final CharacterTextSplitter characterTextSplitter;
    private final CodeTextSplitter codeTextSplitter;
    private final MarkdownTextSplitter markdownTextSplitter;
    private final TokenTextSplitter tokenTextSplitter;
    private final ExcelTextSplitter excelTextSplitter;
    private final PdfProperties pdfProperties;
    private final SysOssMapper sysOssMapper;


    public ResourceLoader getLoaderByFileType(String fileType){
        if (FileType.isTextFile(fileType)){
            return new TextFileLoader(characterTextSplitter);
        } else if (FileType.isWord(fileType)) {
            return new WordLoader(characterTextSplitter);
        } else if (FileType.isPdf(fileType) && pdfProperties.getTransition().isEnableMinerU()) {
            return new PdfMinerUFileLoader(characterTextSplitter,pdfProperties,sysOssMapper);
        } else if (FileType.isPdf(fileType)) {
            return new PdfFileLoader(characterTextSplitter);
        }else if (FileType.isMdFile(fileType)) {
            return new MarkDownFileLoader(markdownTextSplitter);
        }else if (FileType.isCodeFile(fileType)) {
            return new CodeFileLoader(codeTextSplitter);
        } else if (FileType.isExcel(fileType)) {
            return new ExcelFileLoader(excelTextSplitter);
        }else {
            return new TextFileLoader(characterTextSplitter);
        }
    }
}

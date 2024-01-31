package com.welgram.crawler.validator;

import com.welgram.crawler.general.CrawlingProduct;
import java.util.Optional;

public interface PostValidator {

    String getValidatorName();

    Optional<String> validateAndAddErrorMsg(CrawlingProduct info);
}

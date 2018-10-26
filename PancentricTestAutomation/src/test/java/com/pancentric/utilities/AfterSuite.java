package com.pancentric.utilities;

import com.github.mkolisnyk.cucumber.reporting.CucumberDetailedResults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD })
public @interface AfterSuite {

}
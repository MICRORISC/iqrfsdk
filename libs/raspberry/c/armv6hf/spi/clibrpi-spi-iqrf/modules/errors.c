/* 
 * Copyright 2015 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @file       errors.c
 * @author     Michal Konopa
 * @version    1.0.0
 * @date       25.4.2013
 */

#include <stdlib.h>
#include <string.h>
#include <commons/errors.h>

/*
 * Returns a copy of specified operation error.
 */
errors_OperError* errors_getErrorCopy(errors_OperError* operError) {
    if (operError == NULL) {
        return NULL;
    }

    errors_OperError* errorCopy = malloc(sizeof (errors_OperError));

    errorCopy->errorCode = operError->errorCode;
    errorCopy->isSystemErrorCode = operError->isSystemErrorCode;

    int descrLen = strlen(operError->descr);
    errorCopy->descr = malloc((descrLen + 1) * sizeof (char));
    strcpy(errorCopy->descr, operError->descr);

    return errorCopy;
}

/*
 * Sets specified operation error to specified values. If both @c isSSystemErrorCode
 * and @c addErrnoString are >= 1, a string, which descibes the @c errorCode will be
 * added at the end of @c userErrorDescr. If @c operError is NULL, new one is created.
 */
void errors_setError(errors_OperError** operError, const char* userErrorDescr,
        int errorCode, int isSystemErrorCode, int addErrnoString) {
    if (operError == NULL) {
        return;
    }

    if ((*operError) == NULL) {
        (*operError) = malloc(sizeof (errors_OperError));
    }

    (*operError)->errorCode = errorCode;
    (*operError)->isSystemErrorCode = isSystemErrorCode;

    int descrLen = 0;

    descrLen += strlen(userErrorDescr);
    if (isSystemErrorCode) {
        if (addErrnoString) {
            descrLen += strlen(strerror(errorCode));
            descrLen += 2; // for colon and space characters
        }
    }

    descrLen += 1; // terminating '\0' character
    (*operError)->descr = malloc(descrLen * sizeof (char));
    strcpy((*operError)->descr, userErrorDescr);

    if (isSystemErrorCode) {
        if (addErrnoString) {
            strcat((*operError)->descr, ": ");
            strcat((*operError)->descr, strerror(errorCode));
        }
    }
}

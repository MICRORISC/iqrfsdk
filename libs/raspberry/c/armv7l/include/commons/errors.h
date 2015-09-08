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

#ifndef __ERRORS_H_
#define __ERRORS_H_

/**
 * Library, which encapsulates informations about various errors, which can occur during
 * user operations.
 *
 * @file		errors.h
 * @version		1.0
 * @date		25.4.2013
 */

/**
 * Encapsulates information about error, which occurred during user operation.
 */
typedef struct errors_OperError {
    int errorCode; /**< error code */
    int isSystemErrorCode; /**< indicates, wheather the error code designates system error code */
    char* descr; /**< description of the error */
} errors_OperError;


/**
 * Returns a copy of specified operation error. If the specified error is @c NULL,
 * @c NULL is returned.
 * @return copy of specified operation error
 * @return @c NULL, if the specified error is @c NULL
 */
extern errors_OperError* errors_getErrorCopy(errors_OperError* operError);


/**
 * Sets specified operation error to specified values. If both @c isSSystemErrorCode
 * and @c addErrnoString are >= 1, a string, which describes the @c errorCode will be
 * added at the end of @c userErrorDescr.
 * If pointer to operation error is @c NULL, new operation error will be created.
 * If @c operError is @c NULL, nothing is done.
 * @param @c operError pointer to pointer to operation error to set. If @c NULL, nothing
 *           is done.
 * @param @c userErrorDescr user string, describing the error
 * @param @c errorCode error code
 * @param @c isSystemErrorCode indication of system error code
 * @param @c addErrnoString indicates, whether to add an error description to user string.
 *           Applies only for system errors.
 */
extern void errors_setError(
        errors_OperError** operError, const char* userErrorDescr, int errorCode,
        int isSystemErrorCode, int addErrnoString
        );

#endif // __ERRORS_H_

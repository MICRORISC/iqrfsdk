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

#ifndef __BASE_TYPES_H_
#define __BASE_TYPES_H_

/**
 * Base types.
 *
 * @file		base_types.h
 * @version		1.0
 * @date		22.8.2014
 */

/**
 * Results of operations.
 */
typedef enum base_OperResult {
    BASE_TYPES_OPER_OK = 0, /**< an operation has performed correctly */
    BASE_TYPES_OPER_ERROR = -1, /**< an error has occured during operation */
    BASE_TYPES_LIB_NOT_INITIALIZED = -2 /**< a library was not initialized */
} base_OperResult;

#endif // __BASE_TYPES_H_

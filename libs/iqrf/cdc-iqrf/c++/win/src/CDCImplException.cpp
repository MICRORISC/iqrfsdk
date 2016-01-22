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

#include <cdc/CDCImplException.h>


void CDCImplException::createDescription() {
    descr.clear();
	descr.append(identity);
	descr.append(": ");
	descr.append(cause);
}

CDCImplException::CDCImplException(const char* cause) {
	this->identity = "CDCImplException";
	this->cause = cause;
	createDescription();
}

CDCImplException::~CDCImplException() throw() {
	identity.clear();
	cause.clear();
	descr.clear();
}

/*
 * Returns the cause of this exception.
 */
const char* CDCImplException::what() const throw() {
	return cause.c_str();
}

/*
 * Returns complete description of this exception.
 */
const char* CDCImplException::getDescr() {
	return descr.c_str();
}

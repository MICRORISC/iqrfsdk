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
 * Exceptions for CDCImpl class.
 *
 * @author		Michal Konopa
 * @file		CDCImplException.h
 * @version		1.0
 * @date		17.12.2011
 */

#ifndef __CDCImplException_h_
#define __CDCImplException_h_

#include <exception>
#include <string>


/**
 * Base class of exceptions, that may occur during running of
 * CDCImpl objects.
 */
class CDCImplException : std::exception {
private:
	/* Identity string of this exception class. */
	std::string identity;

	/* Complete desription of exception. */
	std::string descr;

	/* Creates desription. */
	void createDescription();

protected:
	/* Cause of exception. */
	std::string cause;

public:
	/**
	 * Constructs exception object.
	 * @param cause description of exception cause.
	 */
	CDCImplException(const char* cause);

	~CDCImplException() throw();

	/**
	 * Returns description of exception cause.
	 * @return description of this exception cause.
	 */
	const char* what() const throw();

	/**
	 * Returns complete description of this exception.
	 * @return complete information of this exception.
	 */
	const char* getDescr();
};

#endif // __CDCImplException_h_

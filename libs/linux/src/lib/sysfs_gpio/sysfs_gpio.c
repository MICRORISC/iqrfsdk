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

#include <stdio.h>
#include <string.h>
#include <errno.h>
#include "sysfs_gpio.h"

/* constants */
#define GPIO_BASE_PATH "/sys/class/gpio"
#define GPIO_EXPORT_PATH GPIO_BASE_PATH"/export"
#define GPIO_UNEXPORT_PATH GPIO_BASE_PATH"/unexport"

#define GPIO_DIRECTION_STR "direction"
#define GPIO_VALUE_STR "value"
#define GPIO_UNEXPORT_STR "unexport"

/* gpio direction state */
#define GPIO_DIRECTION_IN_STR "in"
#define GPIO_DIRECTION_OUT_STR "out"

/** helper functions */
static void setup_gpio_path(const int gpio, const char *action, char *target, int len)
{
	snprintf(target, len, GPIO_BASE_PATH"/gpio%d/%s", gpio, action);
}

static int write_data(FILE *fd, const char *buf)
{
	int ret = 0;

	ret = fwrite(buf, 1, strlen(buf), fd);
	if (ret != strlen(buf)) {
		printf("Error during writing to file\n");
		ret = -1;
	} else {
		ret = 0;
	}

	return ret;
}
int gpio_export(int num)
{
	FILE *fd = fopen(GPIO_EXPORT_PATH, "w");
	char buf[5];
	int ret;
	
	if (!fd) {
		printf("Error during opening file: %s\n", strerror(errno));
		return -1;
	}

	snprintf(buf, sizeof(buf), "%d", num);
	ret = write_data(fd, buf);
	if (ret)
		goto err;
	
err:
	fclose(fd);
	return ret;
}

int gpio_unexport(int num)
{
	FILE *fd = fopen(GPIO_UNEXPORT_PATH, "w");
	char buf[5];
	int ret;
	
	if (!fd) {
		printf("Error during opening file: %s\n", strerror(errno));
		return -1;
	}

	snprintf(buf, sizeof(buf), "%d", num);
	ret = write_data(fd, buf);
	if (ret)
		goto err;

err:
	fclose(fd);
	return ret;
}



int gpio_set_direction(int gpio, enum gpio_direction dir)
{	
	char path[50];
	char buf[4];
	FILE *fd = NULL;
	int ret;
	
	setup_gpio_path(gpio, GPIO_DIRECTION_STR, path, sizeof(path));
	
	fd = fopen(path, "w");
	
	if (!fd) {
		printf("Error during opening file: %s\n", strerror(errno));
		return -1;
	}
	if (dir == GPIO_DIRECTION_IN) {
		strncpy(buf, GPIO_DIRECTION_IN_STR, sizeof(buf));
	} else if (dir == GPIO_DIRECTION_OUT) {
		strncpy(buf,  GPIO_DIRECTION_OUT_STR, sizeof(buf));
	}

	ret = write_data(fd, buf);
	if (ret)
		goto err;

err:
	fclose(fd);
	return ret;
}

enum gpio_direction direction(int gpio)
{
	char path[50];
	char buf[4];
	FILE *fd = NULL;
	int ret;
	enum gpio_direction dir;
	
	setup_gpio_path(gpio, GPIO_DIRECTION_STR, path, sizeof(path));
	
	fd = fopen(path, "r");
	
	if (!fd) {
		printf("Error during opening file: %s\n", strerror(errno));
		return -1;
	}

	ret = fread(buf, 1, sizeof(buf), fd);
	if (!ret) {
		printf("Error during reading file\n");
		ret = -1;
		goto err;
	}

	if (!strcmp(buf, GPIO_DIRECTION_IN_STR)) 
		dir = GPIO_DIRECTION_IN;
	else if (!strcmp(buf, GPIO_DIRECTION_OUT_STR))
		dir = GPIO_DIRECTION_OUT;

	ret = 0;

err:
	fclose(fd);
	return ret;

}

int gpio_set_value(int gpio, int val)
{	
	char path[50];
	char buf[2];
	FILE *fd = NULL;
	int ret;
	
	setup_gpio_path(gpio, GPIO_VALUE_STR, path, sizeof(path));
	
	fd = fopen(path, "w");
	
	if (!fd) {
		printf("Error during opening file: %s\n", strerror(errno));
		return -1;
	}

	snprintf(buf, sizeof(buf), "%d", val);
	ret = write_data(fd, buf);
	if (ret)
		goto err;
	
err:
	fclose(fd);
	return ret;
}

int value(int gpio)
{	
	char path[50];
	char buf[2];
	FILE *fd = NULL;
	int ret;
	
	setup_gpio_path(gpio, GPIO_VALUE_STR, path, sizeof(path));
	
	fd = fopen(path, "r");
	
	if (!fd) {
		printf("Error during opening file: %s\n", strerror(errno));
		return -1;
	}
	
	ret = fread(buf, 1, sizeof(buf), fd);
	if (!ret) {
		printf("Error during reading file\n");
		ret = -1;
		goto err;
	}

	ret = strtol(buf, NULL, 10);

err:
	fclose(fd);
	return ret;
}

int setup_gpio(int gpio, enum gpio_direction dir, int val)
{
	int ret;

	ret = gpio_export(gpio);
	if (ret)
		return ret;
	
	ret = gpio_set_direction(gpio, dir);
	if (ret)
		return ret;
	
	// set value when output direction
	if (dir == GPIO_DIRECTION_OUT) {
		ret = gpio_set_value(gpio, val);
		if (ret)
			return ret;
	}
}

void cleanup_gpio(int gpio)
{
	gpio_set_value(gpio, 0);
	gpio_unexport(gpio);
}


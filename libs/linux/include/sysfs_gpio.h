#ifndef SYSFS_GPIO_H
#define SYSFS_GPIO_H

enum gpio_direction {
	GPIO_DIRECTION_IN = 0,
	GPIO_DIRECTION_OUT
};

// export/unexport gpio
int gpio_export(int num);
int gpio_unexport(int num);

// set/get direction
int gpio_set_direction(int gpio, enum gpio_direction dir);
enum gpio_direction direction(int gpio);

// set/get value
int gpio_set_value(int gpio, int val);
int value(int gpio);

// helper to setup gpio in one funtion
int setup_gpio(int gpio, enum gpio_direction dir, int val);

// cleanup gpio
void cleanup_gpio(int gpio);

#endif // SYSFS_GPIO_H


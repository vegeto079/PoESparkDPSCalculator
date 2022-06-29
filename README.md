# Path of Exile Spark DPS Calculator
It is notoriously difficult to calculate Spark's actual DPS and hits-per-second. The best way I could figure out how to do it was to emulate it to get some close-to-real-world results.

## Deployment
1. Ensure you have Java installed
1. Download the latest [Release](https://github.com/vegeto079/PoESparkDPSCalculator/releases)
1. Run **PoESparkDPSCalculator.jar**

## Controls
* Left click anywhere to move where you are standing.
* Right click anywhere to move where the enemy is standing.
* Middle click to enable/disable casting
* Change any of the parameters on the **Spark Options** menu to change the parameters of your Sparks

## Notes
* Each Spark projectile in a single cast can only hit once every 0.66s. This is indicated by all of the projectiles within that cast turning Red whenever they hit.
* Spark projectiles pick a random direction to move in after hitting a wall. I am not sure how exactly PoE implements this, so there is likely large variance with this factor.

## Examples
![Animated gif of application running](https://i.imgur.com/RiPcg9a.gif)
![Image of application](https://i.imgur.com/3YsKSww.png)

![Image of settings menu](https://user-images.githubusercontent.com/24538801/176362788-2f34bc4e-a097-458f-985e-89150f42bc3d.png)

Go Crazy!

![Picture of application with settings turned up](https://i.imgur.com/3h8cqgz.png)

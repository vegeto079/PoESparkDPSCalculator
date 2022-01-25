# Path of Exile Spark DPS Calculator
It is notoriously difficult to calculate Spark's actual DPS and hits-per-second. The best way I could figure out how to do it was to emulate it to get some close-to-real-world results.

## Deployment
1. Download and compile [ng-commontools](https://github.com/vegeto079/ng-commontools)
2. Add CommonTools and it's libraries as a dependency to PoESparkDPSCalculator
3. Compile PoESparkDPSCalculator
4. Run SparkDPSCalculator.java

## Controls
* Left click anywhere to move where you are standing.
* Right click anywhere to move where the enemy is standing.

## Notes
* Each Spark projectile in a single cast can only hit once every 0.66s. This is indicated by all of the projectiles within that cast turning Red whenever they hit.
* Spark projectiles pick a random direction to move in after hitting a wall. I am not sure how exactly PoE implements this, so there is likely large variance with this factor.

## Examples
![Gif of application running](https://i.imgur.com/RiPcg9a.gif)
![Picture of application](https://i.imgur.com/3YsKSww.png)

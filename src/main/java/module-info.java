open module mascot {
    requires beast.pkgmgmt;
    requires beast.base;
    requires static beast.fx;
    requires static javafx.controls;
    requires org.apache.commons.math4.legacy;
    requires java.desktop;

    exports mascot.app.beauti;
    exports mascot.distribution;
    exports mascot.dynamics;
    exports mascot.glmmodel;
    exports mascot.logger;
    exports mascot.mapped;
    exports mascot.ode;
    exports mascot.operators;
    exports mascot.parameterdynamics;
    exports mascot.skyline;
    exports mascot.util;

    provides beast.base.core.BEASTInterface with
        // distribution
        mascot.distribution.Mascot,
        mascot.distribution.MascotWithTipSampling,
        mascot.distribution.MappedMascot,
        mascot.distribution.MappedMascotWithTipSampling,
        mascot.distribution.StructuredTreeDistribution,
        mascot.distribution.StructuredTreeIntervals,
        // dynamics
        mascot.dynamics.Constant,
        mascot.dynamics.ConstantBSSVS,
        mascot.dynamics.DynamicEffectivePopulationSizesBSSVS,
        mascot.dynamics.GLM,
        mascot.dynamics.GLMWithSkylineNe,
        mascot.dynamics.RateShifts,
        mascot.dynamics.StructuredMigrationSkyline,
        mascot.dynamics.StructuredSkyline,
        // glmmodel
        mascot.glmmodel.Covariate,
        mascot.glmmodel.CovariateList,
        mascot.glmmodel.ErrorSmoothing,
        mascot.glmmodel.LogLinear,
        mascot.glmmodel.MaxRate,
        // logger
        mascot.logger.MigrationCountLogger,
        mascot.logger.MigrationCountLoggerWithTipSampling,
        mascot.logger.RootStateLogger,
        mascot.logger.StructuredTreeLogger,
        mascot.logger.mappedProbLogger,
        // mapped
        mascot.mapped.AncestralMappedTreeSequenceLogger,
        mascot.mapped.AncestralStateTreeLikelihood,
        mascot.mapped.MappedTreeIntervals,
        mascot.mapped.MutationTableLogger,
        mascot.mapped.MutationTimeLogger,
        // operators
        mascot.operators.BactrianBlockRandomWalkOperator,
        mascot.operators.BooleanSwapOperator,
        mascot.operators.MultiRealRandomWalkOperator,
        mascot.operators.NeSwapper,
        mascot.operators.RealRandomWalkOperator,
        mascot.operators.SwapOperator,
        // parameterdynamics
        mascot.parameterdynamics.ConstantNe,
        mascot.parameterdynamics.ExponentialNe,
        mascot.parameterdynamics.LogisticNe,
        mascot.parameterdynamics.LogLinearGLM,
        mascot.parameterdynamics.NeDynamicsList,
        mascot.parameterdynamics.NeSplineInterpolation,
        mascot.parameterdynamics.NotSet,
        mascot.parameterdynamics.Skygrowth,
        mascot.parameterdynamics.StructuredSkygrid,
        // skyline
        mascot.skyline.GLMPrior,
        mascot.skyline.GrowthRateSmoothingPrior,
        mascot.skyline.LogSmoothingPrior,
        // util
        mascot.util.Difference,
        mascot.util.Final,
        mascot.util.First,
        mascot.util.GLMLogger,
        mascot.util.InitializedCovariate,
        mascot.util.InitializedCovariatesList,
        mascot.util.InitializedGlmModel,
        mascot.util.InitializedNeDynamicsList,
        mascot.util.InitializedRateShifts,
        mascot.util.InitializedRateShiftsForSkyline,
        mascot.util.InitializedTraitSet,
        mascot.util.LargerThan,
        mascot.util.Mean,
        // beauti
        mascot.app.beauti.TreeWithTrait,
        mascot.app.beauti.TreeWithTraitInitializer;

    // InputEditor providers are registered via version.xml only
    // (InputEditor.Base does not have a no-arg constructor required by JPMS provides)
}

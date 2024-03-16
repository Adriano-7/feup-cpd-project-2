function on_mult(m_ar::Int, m_br::Int)
    pha = ones(Float64, m_ar, m_ar)
    phb = [Float64(i+1) for i in 0:m_br-1]
    phc = zeros(Float64, m_ar, m_br)

    elapsed_time = @elapsed begin
        for i in 1:m_ar
            for j in 1:m_br
                temp = 0.0
                for k in 1:m_ar
                    temp += pha[i, k] * phb[k]
                end
                phc[i, j] = temp
            end
        end
    end

    println("Time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()

    return elapsed_time
end

function on_mult_line(m_ar::Int, m_br::Int)
    pha = ones(Float64, m_ar, m_ar)
    phb = [Float64(i+1) for i in 0:m_br-1]
    phc = zeros(Float64, m_ar, m_br)

    elapsed_time = @elapsed begin
        for i in 1:m_ar
            for k in 1:m_ar
                for j in 1:m_br
                    phc[i, j] += pha[i, k] * phb[k]
                end
            end
        end
    end

    println("Time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()
    return elapsed_time
end

function on_mult_block(m_ar::Int, m_br::Int, block_size::Int)
    pha = ones(Float64, m_ar, m_ar)
    phb = [Float64(i+1) for i in 0:m_br-1]
    phc = zeros(Float64, m_ar, m_br)
    
    elapsed_time = @elapsed begin
        for row_block_start in 1:bk_size:m_ar
            for col_block_start in 1:bk_size:m_ar
                for inner_block_start in 1:bk_size:m_ar
                    for row in row_block_start:min(row_block_start+bk_size-1, m_ar)
                        for col in col_block_start:min(col_block_start+bk_size-1, m_ar)
                            for inner in inner_block_start:min(inner_block_start+bk_size-1, m_ar)
                                phc[row, inner] += pha[row, col] * phb[col]
                            end
                        end
                    end
                end
            end
        end
    end

    println("Time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()
    return elapsed_time
end

function main()
    outputFile = open("resultsMultJulia.csv", "w")
    println(outputFile, "Try,Dimension,Time,L1_DCM,L2_DCM")

    #=
    for trial in 0:9
        # From 600 to 3000, step 400 (OnMult)
        for dim in 600:400:3000
            println("Trial: $trial")
            println("Dimension: $dim")

            elapsed_time = on_mult(dim, dim)  

            # Write results to the CSV file
            println(outputFile, "$trial,$dim,$elapsed_time") 

            println()
        end
    end
=#
    # From 600 to 3000, step 400 (OnMultLine)
    outputFile = open("resultsMultLineJulia.csv", "w")
    println(outputFile, "Try,Dimension,Time")

    for trial in 0:9
        for dim in 600:400:3000
            println("Trial: $trial")
            println("Dimension: $dim")

            elapsed_time = on_mult_line(dim, dim)  

            println(outputFile, "$trial,$dim,$elapsed_time")  

            println()
        end
    end
#=
    # From 4096 to 10240 with intervals of 2048 for block sizes (128, 256, 512) (OnMultBlock)
    outputFile = open("resultsMultBlockJulia.csv", "w")
    println(outputFile, "Try,Dimension,BlockSize,Time")

    for trial in 0:9
        for dim in 4096:2048:10240
            for blkSize in [128, 256, 512]
                println("Trial: $trial")
                println("Dimension: $dim")
                println("BlockSize: $blkSize")

                elapsed_time = on_mult_block(dim, dim, blkSize) 

                println(outputFile, "$trial,$dim,$blkSize,$elapsed_time")
                println()
            end
        end
    end
=#
    close(outputFile)
end

main()
